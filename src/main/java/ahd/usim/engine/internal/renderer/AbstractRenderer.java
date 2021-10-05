package ahd.usim.engine.internal.renderer;

import ahd.ulib.utils.annotation.NotFinal;
import ahd.usim.engine.internal.api.Renderer;
import ahd.usim.engine.internal.api.Visible;

public abstract class AbstractRenderer implements Renderer {

    protected AbstractRenderer() {
    }

    public void addTargets(Shader shader, Visible... targets) {
//        var list = this.targets.getOrDefault(shader, new ArrayList<>());
//        list.addAll(Arrays.asList(targets));
//        this.targets.put(shader, list);
    }

    public void addTargets(String shaderName, Visible... targets) {
//        var shader = shaderMap.get(shaderName);
//        if (shader == null)
//            throw new RuntimeException("AHD:: A shader must be created before adding target(s) to it.");
//        addTargets(shader, targets);
    }

    @NotFinal(issues = { NotFinal.REIMPLEMENT_NEEDED, NotFinal.CONCURRENCY, NotFinal.PERFORMANCE })
    public void cleanupShader(Shader shader) {
//        var tmp = new HashMap<>(shaderMap);
//        for (var kv : shaderMap.entrySet())
//            if (kv.getValue().equals(shader))
//                tmp.remove(kv.getKey());
//        shaderMap.clear();
//        shaderMap.putAll(tmp);
//        targets.remove(shader);
//        shader.cleanup();
    }

    public void cleanupShader(String shaderName) {
//        var shader = shaderMap.get(shaderName);
//        shaderMap.clear();
//        targets.remove(shader);
//        shader.cleanup();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void render() {

    }
}
