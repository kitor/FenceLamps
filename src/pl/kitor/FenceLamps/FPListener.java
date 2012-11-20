package pl.kitor.FenceLamps;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;

public class FPListener implements Listener {

    private static final int TORCH_ON = 76;
    private static final int REDLAMP_ON = 124;
    int FENCE;
    int LAMP_OFF;                                                               //20 - glass
    int LAMP_ON;                                                                //89 - glowstone
    byte LAMP_DATA;                                                             //data value for our block
    int MAX_V;                                                                  //maximum height that current goes

    @SuppressWarnings("LeakingThisInConstructor")
    public FPListener(FenceLamps plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void doNotStole(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        if (block.getTypeId() == LAMP_ON
                && block.getData() == LAMP_DATA) {
            System.out.println("DropEvent");
            block.setTypeId(0);
            ItemStack drop = new ItemStack(LAMP_OFF, 1);
            World world = block.getWorld();
            world.dropItemNaturally(block.getLocation(), drop);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void redLampFix(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (LAMP_ON == REDLAMP_ON //fire only if needed
                && block.getTypeId() == REDLAMP_ON
                && block.getData() == LAMP_DATA
                && block.getRelative(0, -1, 0).getTypeId() == FENCE) {
            event.setNewCurrent(5);                                             //set 'lost' currrent
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    @SuppressWarnings("empty-statement")
    public void normalLogin(BlockPhysicsEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block fence = event.getBlock();
        if (fence.getTypeId() != FENCE) {
            return;
        }

        Block source = fence.getRelative(0, -1, 0);                             //gets block under the fence

        if ((source.getTypeId() == 0) || (source.getTypeId() == FENCE)) {       //if it's air or fence - do nothing
            return;
        }

        boolean state = source.isBlockPowered();

        if (source.getTypeId() == TORCH_ON) {                                   //redstone source itself is not a powered block
            state = true;
        }

        int height = 0;
        while (source.getRelative(0, ++height, 0).getTypeId() == FENCE);        //count fence height
        if ((height > 1) && (height <= MAX_V + 1)) {                            //check maximum height
            Block lamp = source.getRelative(0, height, 0);

            int lampId = lamp.getTypeId();
            if ((lampId == LAMP_OFF) || (lampId == LAMP_ON)) {
                if (((state == true) && (lampId == LAMP_ON))
                        || ((state == false) && (lampId == LAMP_OFF))) {
                    return;                                                     //if states are equal - do nothing
                }
                if ((lampId == LAMP_ON) && (lamp.getData() != LAMP_DATA)) {
                    return;                                                     //if it's not our block - do nothing;
                }
                lamp.setTypeId(state == true ? LAMP_ON : LAMP_OFF);
                lamp.setData(LAMP_DATA);
            }
        }
    }
}
