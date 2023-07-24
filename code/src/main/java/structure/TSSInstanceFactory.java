package structure;

import grafo.optilib.structure.InstanceFactory;

public class TSSInstanceFactory extends InstanceFactory<TSSInstance> {

    @Override
    public TSSInstance readInstance(String s) {
        return new TSSInstance(s);
    }
}
