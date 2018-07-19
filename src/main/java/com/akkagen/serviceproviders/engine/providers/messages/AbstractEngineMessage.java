package com.akkagen.serviceproviders.engine.providers.messages;

import akka.dispatch.sysmsg.Create;
import com.akkagen.models.AbstractEngineDefinition;

public abstract class AbstractEngineMessage {
    private AbstractEngineDefinition def;
    public AbstractEngineMessage(AbstractEngineDefinition def){this.def = def;}
    public AbstractEngineDefinition getDef() {return def;}

    public static class CreateEngine extends AbstractEngineMessage{
        public CreateEngine(AbstractEngineDefinition def){super(def);}
    }
    public static class UpdateEngine extends AbstractEngineMessage{
        public UpdateEngine(AbstractEngineDefinition def){super(def);}
    }
    public static class DeleteEngine extends AbstractEngineMessage{
        public DeleteEngine(AbstractEngineDefinition def){super(def);}
    }
}
