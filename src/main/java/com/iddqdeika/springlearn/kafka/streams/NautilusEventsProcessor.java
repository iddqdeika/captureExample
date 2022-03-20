package com.iddqdeika.springlearn.kafka.streams;

import com.iddqdeika.springlearn.domain.ItemWithStructureMaps;
import com.iddqdeika.springlearn.kafka.serdes.CaptureEventSerde;
import com.iddqdeika.springlearn.kafka.serdes.ItemWithStructureMapsSerde;
import com.iddqdeika.springlearn.domain.CaptureEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.NamedInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NautilusEventsProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<CaptureEvent> CAPTURE_EVENT_SERDE = new CaptureEventSerde();
    private static final Serde<ItemWithStructureMaps> ITEM_WITH_STRUCTURE_MAPS_SERDE = new ItemWithStructureMapsSerde();

    static final String STRUCTURE_MAPS_TABLE = "[HPM_MASTER].dbo.ArticleStructureMap";
    static final String STRUCTURE_MAPS_FK = "ArticleRevisionID";

    static final String REVISION_TABLE = "[HPM_MASTER].dbo.ArticleRevision";
    static final String REVISION_TABLE_KEY = "ID";

    static final String TOPIC = "item-storage.nautilus.cdc.ArticleStructureMap-test01";
    static final String OUTPUT_TOPIC = "output";

    @Autowired
    void buildNautilusPipeline(StreamsBuilder streamsBuilder){
        KStream<String, CaptureEvent> stream = streamsBuilder
                .stream(TOPIC, Consumed.with(STRING_SERDE, CAPTURE_EVENT_SERDE));
        KTable<String, ItemWithStructureMaps> maps = aggregateStructureMapsByArticleId(stream);
        KTable<String, CaptureEvent> articles = storeArticlesById(stream);
        joinAndOutput(maps, articles);
    }

    /**
     * Собирает из потока только события ArticleRevision в KTable, чтобы иметь хранилище с существующими ArticleRevision
     * @param stream - поток ивентов CaptureEvent
     * @return таблица, в которой по ArticleId хранится последнее состояние этой записи (CaptureEvent)
     */
    private KTable<String, CaptureEvent> storeArticlesById(KStream<String, CaptureEvent> stream) {
        return stream
                .filter((s, captureEvent) -> captureEvent.getTable().equals(REVISION_TABLE)
                        && captureEvent.getValues() != null)
                .map((s, captureEvent) -> KeyValue.pair(captureEvent.getValues().get(REVISION_TABLE_KEY), captureEvent))
                .peek(this::printEvent)
                .toTable(NamedInternal.empty(), Materialized.with(STRING_SERDE, CAPTURE_EVENT_SERDE));
    }

    /**
     * Собираем из потока только события ArticleStructureMap и аггрегирует в ItemWithStructureMaps
     * ItemWithStructureMaps может содержать несколько привязок,
     * поэтому все новые привязки добавляются в агрегат через метод
     * @param stream поток изменений
     * @return табличка с аггрегированными привязками. ключом является ArticleRevisionID
     */
    private KTable<String, ItemWithStructureMaps> aggregateStructureMapsByArticleId(KStream<String, CaptureEvent> stream) {
        return stream
                .filter((s, captureEvent) -> captureEvent.getTable().equals(STRUCTURE_MAPS_TABLE)
                        && captureEvent.getValues() != null)
                .map((s, captureEvent) ->
                        KeyValue.pair(captureEvent.getValues().get(STRUCTURE_MAPS_FK), captureEvent))
                .peek(this::printEvent)
                .groupByKey(Serialized.with(STRING_SERDE, CAPTURE_EVENT_SERDE))
                .aggregate(ItemWithStructureMaps::new,
                        (s, captureEvent, itemWithStructureMaps) ->
                                itemWithStructureMaps.withId(s).addMap(captureEvent),
                        Materialized.with(STRING_SERDE, ITEM_WITH_STRUCTURE_MAPS_SERDE));
    }

    /**
     * Джойнит игрегаты привязок с информацией о позициях (добавляя в аггрегат актуальный ивент позиции)
     * и выводит изменения в аутпут топик
     * @param maps - табличка с аггрегированными привязками
     * @param articles - табличка с данными ArticleRevision
     */
    private void joinAndOutput(KTable<String, ItemWithStructureMaps> maps, KTable<String, CaptureEvent> articles) {
        maps.join(articles, ItemWithStructureMaps::withRevision)
                .toStream(Named.as("joinedResult"))
                .peek(this::printResult)
                .to(OUTPUT_TOPIC, Produced.with(STRING_SERDE, ITEM_WITH_STRUCTURE_MAPS_SERDE));
    }

    /**
     * печатаем событие в консоль (херь для дебага)
     * @param key ключ события
     * @param value событие CaptureEvent
     */
    void printEvent(String key, CaptureEvent value){
        System.out.println("event with key " + key + " for table " + value.getTable());
    }

    /**
     * печатаем результирующий аггрегат в консоль (херь для дебага)
     * @param key - ключ результата
     * @param val - результат
     */
    private void printResult(String key, ItemWithStructureMaps val) {
        System.out.println(val.toString());
    }

}
