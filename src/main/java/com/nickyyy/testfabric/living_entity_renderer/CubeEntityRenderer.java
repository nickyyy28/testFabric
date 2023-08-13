package com.nickyyy.testfabric.living_entity_renderer;

import com.nickyyy.testfabric.living_entity.CubeEntity;
import com.nickyyy.testfabric.model.CubeEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CubeEntityRenderer extends MobEntityRenderer<CubeEntity, CubeEntityModel> {
    public CubeEntityRenderer(EntityRendererFactory.Context context, CubeEntityModel entityModel) {
        super(context, entityModel, 0.5f);
    }

    public CubeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CubeEntityModel(), 0.5f);
    }

    @Override
    public Identifier getTexture(CubeEntity entity) {
        return new Identifier("testfabric", "textures/entity/cube/cube.png");
    }
}
