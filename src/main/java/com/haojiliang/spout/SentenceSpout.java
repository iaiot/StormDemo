package com.haojiliang.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SentenceSpout extends BaseRichSpout {

    private ConcurrentHashMap<UUID, Values> pending;
    private SpoutOutputCollector collector;
    private String[] sentences = {"my dog has fleas",
            "i like cold beverages",
            "the dog ate my homework",
            "don't have a coe man",
            "i don't think i like fleas"};
    private int index = 0;

    @Override
    public void ack(Object msgId) {
        this.pending.remove(msgId);
    }

    @Override
    public void fail(Object msgId) {
        this.collector.emit(this.pending.get(msgId), msgId);
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        this.pending = new ConcurrentHashMap<>();
    }

    @Override
    public void nextTuple() {
        Values values = new Values(sentences[index]);
        UUID msgId = UUID.randomUUID();
        this.pending.put(msgId, values);
        this.collector.emit(values, msgId);
        index++;
        if (index >= sentences.length) {
            index = 0;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentence"));
    }
}
