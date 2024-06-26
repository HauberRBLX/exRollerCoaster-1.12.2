package erc.entity;

import java.util.Random;

import erc._core.ERC_Logger;
import erc.renderer.ModelRenderer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc._core.ERC_CONST;
import erc._core.ERC_Core;
import erc.math.ERC_MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class entitySUSHI extends Entity {
	private static final DataParameter<Integer> ID = EntityDataManager.<Integer>createKey(entitySUSHI.class, DataSerializers.VARINT);
	private static final DataParameter<Float> ROT = EntityDataManager.<Float>createKey(entitySUSHI.class, DataSerializers.FLOAT);

	@SideOnly(Side.CLIENT)
	public static OBJModel model1;
	@SideOnly(Side.CLIENT)
	public static OBJModel model2;
	@SideOnly(Side.CLIENT)
	public static OBJModel model3;
	@SideOnly(Side.CLIENT)
	public static OBJModel model4;
	@SideOnly(Side.CLIENT)
	public static OBJModel model5;
	@SideOnly(Side.CLIENT)
	public static OBJModel[] models;

	@SideOnly(Side.CLIENT)
	public static void clientInitSUSHI()
	{
		try {
			model1 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_m.obj"));
			model2 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_t.obj"));
			model3 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_w.obj"));
			model4 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_e.obj"));
			model5 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_g.obj"));
		}
		catch(Exception e){
			ERC_Logger.warn("Loading sushi model is failure");
		}
		models = new OBJModel[5];
		models[0] = model2;
		models[1] = model3;
		models[2] = model4;
		models[3] = model5;
		models[4] = model1;
	}
	
	float rotation;
	float prevRotation;
	
	public entitySUSHI(World world)
	{
		super(world);
		setSize(0.9f, 0.4f);
	}

	public entitySUSHI(World world, double posX, double posY, double posZ)
	{
		this(world);
		setPosition(posX, posY, posZ);
	}

	public float getInterpRotation(float partialTicks)
	{
		return this.prevRotation + (this.rotation - this.prevRotation) * partialTicks;
	}

	public OBJModel getModel() { return models[this.getId()];}
	
	@Override
	protected void entityInit()
	{
		Random r = new Random();
		
		this.dataManager.register(ROT, 0f);
//		this.dataManager.addObject(21, new Integer(1));
		this.dataManager.register(ID, (int) Math.floor(r.nextInt(44)/10d));
	}
	
	@Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }
	
	@Override
    public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
    	boolean flag = ds.getTrueSource() instanceof EntityPlayer;

	    if (flag)
	    {
	        setDead();
	        boolean flag1 = ((EntityPlayer)ds.getTrueSource()).capabilities.isCreativeMode;
	        if(!flag1 && !world.isRemote)entityDropItem(new ItemStack(ERC_Core.ItemSUSHI,1,0), 0f);
	    }
	    
    	return false;
    }
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
	{
		if(player.isSneaking())
		{
			setRot(getRot()*1.1f);
		}
		else
		{
			if(getRot()==0)setRot(3.0f);
			else if(getRot()>0)setRot(-3.0f);
			else if(getRot()<0)setRot(0);
		}
		player.swingArm(hand);
		return false;
	}
	
	public void onUpdate()
	{
//		setDead();
		prevRotation = rotation;
		rotation += getRot();
		ERC_MathHelper.fixrot(rotation, prevRotation);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setRot(nbt.getFloat("speed"));
		int id = nbt.getInteger("modelid");
		if(id>0)setId(id);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("speed",getRot());
		nbt.setInteger("modelid",getId());
	}

	public float getRot()
	{
		return this.dataManager.get(ROT);
	}
	public void setRot(float rot)
	{
		this.dataManager.set(ROT, rot);
	}
	
	public int getId()
	{
		return this.dataManager.get(ID);
	}
	public void setId(int id)
	{
		this.dataManager.set(ID, id);
	}
}
