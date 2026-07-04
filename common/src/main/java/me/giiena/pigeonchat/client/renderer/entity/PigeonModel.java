package me.giiena.pigeonchat.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class PigeonModel extends EntityModel<PigeonRenderState> {
	public final ModelPart Head;
	public final ModelPart Beak;
	public final ModelPart Body;
	public final ModelPart Tail;
	public final ModelPart LWing;
	public final ModelPart RWing;
	public final ModelPart Legs;

	public PigeonModel(ModelPart root) {
		super(root);
		this.Head = root.getChild("Head");
		this.Beak = root.getChild("Beak");
		this.Body = root.getChild("Body");
		this.Tail = root.getChild("Tail");
		this.LWing = root.getChild("LWing");
		this.RWing = root.getChild("RWing");
		this.Legs = root.getChild("Legs");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -0.1F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(13, 2).addBox(-1.5F, 0.0F, -0.1F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.5F, -4.0F));
		partdefinition.addOrReplaceChild("Beak", CubeListBuilder.create().texOffs(26, 4).addBox(-0.5F, -10.0F, -5.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.25F, 0.0F, 0.4363F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(4, 19).addBox(-1.5F, -1.75F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 21.75F, 1.75F, 0.6109F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("LWing", CubeListBuilder.create().texOffs(17, 9).addBox(2.0F, -8.75F, -1.5F, 1.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 23.75F, 0.75F, 0.4363F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("RWing", CubeListBuilder.create().texOffs(17, 9).addBox(-3.0F, -8.75F, -1.5F, 1.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 23.75F, 0.75F, 0.4363F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("Legs", CubeListBuilder.create().texOffs(17, 20).addBox(-1.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(17, 20).addBox(0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
}