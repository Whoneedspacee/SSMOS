package SSM.GameManagers;

import SSM.SSM;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BlockRestoreManager implements Listener, Runnable {

    public static BlockRestoreManager ourInstance;
    private JavaPlugin plugin = SSM.getInstance();
    private HashMap<Block, BlockRestoreData> blocks = new HashMap<>();
    private LinkedList<BlockRestoreMap> restoreMaps;

    public BlockRestoreManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
        restoreMaps = new LinkedList<BlockRestoreMap>();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 0L);
    }

    @EventHandler(priority= EventPriority.LOW)
    public void blockBreak(BlockBreakEvent event)
    {
        if (contains(event.getBlock()))
        {
            BlockRestoreData data = blocks.get(event.getBlock());
            if (data != null && data.isRestoreOnBreak())
            {
                blocks.remove(event.getBlock());
                data.restore();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void blockPlace(BlockPlaceEvent event)
    {
        if (contains(event.getBlockPlaced()))
            event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.LOW)
    public void piston(BlockPistonExtendEvent event)
    {
        if (event.isCancelled())
            return;

        Block push = event.getBlock();
        for (int i=0 ; i<13 ; i++)
        {
            push = push.getRelative(event.getDirection());

            if (push.getType() == Material.AIR)
                return;

            if (contains(push))
            {
                push.getWorld().playEffect(push.getLocation(), Effect.STEP_SOUND, push.getTypeId());
                event.setCancelled(true);
                return;
            }
        }
    }

    public void run()
    {
        ArrayList<Block> toRemove = new ArrayList<Block>();

        for (BlockRestoreData cur : blocks.values())
            if (cur.expire())
                toRemove.add(cur.block);

        //Remove Handled
        for (Block cur : toRemove)
            blocks.remove(cur);
    }

    @EventHandler
    public void expireUnload(ChunkUnloadEvent event)
    {
        Iterator<Map.Entry<Block, BlockRestoreData>> iterator = blocks.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<Block, BlockRestoreData> entry = iterator.next();
            if (entry.getKey().getChunk().equals(event.getChunk()))
            {
                entry.getValue().restore();
                iterator.remove();
            }
        }
    }

    public boolean restore(Block block)
    {
        if (!contains(block))
            return false;

        blocks.remove(block).restore();
        return true;
    }

    public void restoreAll()
    {
        for (BlockRestoreData data : blocks.values())
            data.restore();

        blocks.clear();
    }

    public HashSet<Location> restoreBlockAround(Material type, Location location, int radius)
    {
        HashSet<Location> restored = new HashSet<Location>();

        Iterator<Block> blockIterator = blocks.keySet().iterator();

        while (blockIterator.hasNext())
        {
            Block block = blockIterator.next();

            if (block.getType() != type)
                continue;

            if (block.getLocation().add(0.5, 0.5, 0.5).distance(location) > radius)
                continue;

            restored.add(block.getLocation().add(0.5, 0.5, 0.5));

            blocks.get(block).restore();

            blockIterator.remove();
        }

        return restored;
    }

    public void add(Block block, int toID, byte toData, long expireTime)
    {
        add(block, toID, toData, expireTime, false);
    }

    public void add(Block block, int toID, byte toData, long expireTime, boolean restoreOnBreak)
    {
        add(block, toID, toData, block.getTypeId(), block.getData(), expireTime, restoreOnBreak);
    }

    public void add(Block block, int toID, byte toData, int fromID, byte fromData, long expireTime)
    {
        add(block, toID, toData, fromID, fromData, expireTime, false);
    }

    public void add(Block block, int toID, byte toData, int fromID, byte fromData, long expireTime, boolean restoreOnBreak)
    {
        if (!contains(block))		getBlocks().put(block, new BlockRestoreData(block, toID, toData, fromID, fromData, expireTime, 0, restoreOnBreak));
        else
        {
            if (getData(block) != null)
            {
                getData(block).update(toID, toData, expireTime);
            }
        }
    }

    public void snow(Block block, byte heightAdd, byte heightMax, long expireTime, long meltDelay, int heightJumps)
    {
        //Fill Above
        if (((block.getTypeId() == 78 && block.getData() >= (byte)7) || block.getTypeId() == 80) && getData(block) != null)
        {
            if (getData(block) != null)
                getData(block).update(78, heightAdd, expireTime, meltDelay);

            if (heightJumps > 0)	snow(block.getRelative(BlockFace.UP), heightAdd, heightMax, expireTime, meltDelay, heightJumps - 1);
            if (heightJumps == -1)	snow(block.getRelative(BlockFace.UP), heightAdd, heightMax, expireTime, meltDelay, -1);

            return;
        }

        //Not Grounded
        if (!block.getRelative(BlockFace.DOWN).getType().isSolid() && block.getRelative(BlockFace.DOWN).getTypeId() != 78)
            return;

        //Not on Solid Snow
        if (block.getRelative(BlockFace.DOWN).getTypeId() == 78 && block.getRelative(BlockFace.DOWN).getData() < (byte)7)
            return;

        //No Snow on Ice
        if (block.getRelative(BlockFace.DOWN).getTypeId() == 79 || block.getRelative(BlockFace.DOWN).getTypeId() == 174)
            return;

        //No Snow on Slabs
        if (block.getRelative(BlockFace.DOWN).getTypeId() == 44 || block.getRelative(BlockFace.DOWN).getTypeId() == 126)
            return;

        //No Snow on Stairs
        if (block.getRelative(BlockFace.DOWN).getType().toString().contains("STAIRS"))
            return;

        //No Snow on Fence or Walls
        if (block.getRelative(BlockFace.DOWN).getType().name().toLowerCase().contains("fence") ||
                block.getRelative(BlockFace.DOWN).getType().name().toLowerCase().contains("wall"))
            return;

        //Not Buildable
        if (!block.getType().isSolid() && block.getTypeId() != 78 && block.getType() != Material.CARPET)
            return;

        //Limit Build Height
        if (block.getTypeId() == 78)
            if (block.getData() >= (byte)(heightMax-1))
                heightAdd = 0;

        //Snow
        if (!contains(block))
            getBlocks().put(block, new BlockRestoreData(block, 78, (byte) Math.max(0, heightAdd - 1), block.getTypeId(), block.getData(), expireTime, meltDelay, false));
        else
        {
            if (getData(block) != null)
                getData(block).update(78, heightAdd, expireTime, meltDelay);
        }
    }

    public boolean contains(Block block)
    {
        if (getBlocks().containsKey(block))
            return true;

        for (BlockRestoreMap restoreMap : restoreMaps)
        {
            if (restoreMap.contains(block))
                return true;
        }

        return false;
    }

    public BlockRestoreData getData(Block block)
    {
        if (blocks.containsKey(block))
            return blocks.get(block);
        return null;
    }

    public Map<Block, BlockRestoreData> getBlocks()
    {
        return blocks;
    }

    public BlockRestoreMap createMap()
    {
        BlockRestoreMap map = new BlockRestoreMap(this);
        restoreMaps.add(map);
        return map;
    }

    protected void removeMap(BlockRestoreMap blockRestore)
    {
        restoreMaps.remove(blockRestore);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        if (blocks.containsKey(event.getBlock()))
        {
            event.setCancelled(true);
        }
    }

    public void disable()
    {
        // Clear all restore maps
        for (BlockRestoreMap restoreMap : restoreMaps)
        {
            restoreMap.restoreInstant();
        }

        restoreAll();
    }

    public static class BlockRestoreData {

        protected Block block;

        protected int fromID;
        protected byte fromData;

        protected int toID;
        protected byte toData;

        protected long expireDelay;
        protected long epoch;

        protected long meltDelay = 0;
        protected long meltLast = 0;

        protected BlockState fromState;

        protected HashMap<Location, Byte> pad = new HashMap<Location, Byte>();

        protected boolean restoreOnBreak;

        public BlockRestoreData(Block block, int toID, byte toData, int fromID, byte fromData, long expireDelay, long meltDelay, boolean restoreOnBreak) {
            this.block = block;
            this.fromState = block.getState();

            this.fromID = fromID;
            this.fromData = fromData;

            this.toID = toID;
            this.toData = toData;

            this.expireDelay = expireDelay;
            this.epoch = System.currentTimeMillis();

            this.meltDelay = meltDelay;
            this.meltLast = System.currentTimeMillis();

            this.restoreOnBreak = restoreOnBreak;

            //Set
            set();
        }

        public boolean expire() {
            if (System.currentTimeMillis() - epoch < expireDelay)
                return false;

            //Try to Melt
            if (melt())
                return false;

            //Restore
            restore();
            return true;
        }

        public boolean melt() {
            if (block.getTypeId() != 78 && block.getTypeId() != 80)
                return false;

            if (block.getRelative(BlockFace.UP).getTypeId() == 78 || block.getRelative(BlockFace.UP).getTypeId() == 80) {
                meltLast = System.currentTimeMillis();
                return true;
            }

            if (System.currentTimeMillis() - meltLast < meltDelay)
                return true;

            //Return to Cover
            if (block.getTypeId() == 80)
                block.setTypeIdAndData(78, (byte) 7, false);

            byte data = block.getData();
            if (data <= 0) return false;

            block.setData((byte) (block.getData() - 1));
            meltLast = System.currentTimeMillis();
            return true;
        }

        public void update(int toIDIn, byte toDataIn) {
            toID = toIDIn;
            toData = toDataIn;

            //Set
            set();
        }

        public void update(int toID, byte addData, long expireTime) {
            //Snow Data
            if (toID == 78) {
                if (toID == 78) toData = (byte) Math.min(7, toData + addData);
                else toData = addData;
            } else
                toData = addData;

            toID = toID;

            //Set
            set();

            //Reset Time
            expireDelay = expireTime;
            epoch = System.currentTimeMillis();
        }

        public void update(int toID, byte addData, long expireTime, long meltDelay) {
            //Snow Data
            if (toID == 78) {
                if (toID == 78) toData = (byte) Math.min(7, toData + addData);
                else toData = addData;
            }

            toID = toID;

            //Set
            set();

            //Reset Time
            expireDelay = expireTime;
            epoch = System.currentTimeMillis();

            //Melt Delay
            if (meltDelay < meltDelay)
                meltDelay = (meltDelay + meltDelay) / 2;
        }

        public void set() {
            if (toID == 78 && toData == (byte) 7)
                block.setTypeIdAndData(80, (byte) 0, true);
            else if (toID == 8 || toID == 9 || toID == 79) {
                handleLilypad(false);
                block.setTypeIdAndData(toID, toData, true);
            } else
                block.setTypeIdAndData(toID, toData, true);
        }

        public boolean isRestoreOnBreak() {
            return restoreOnBreak;
        }

        public void restore() {
            block.setTypeIdAndData(fromID, fromData, true);
            fromState.update();

            handleLilypad(true);
        }

        public void setFromId(int i) {
            fromID = i;
        }

        public void setFromData(byte i) {
            fromData = i;
        }

        private void handleLilypad(boolean restore) {
            if (restore) {
                for (Location l : pad.keySet()) {
                    l.getBlock().setType(Material.WATER_LILY);
                    l.getBlock().setData(pad.get(l));
                }
            } else {
                if (block.getRelative(BlockFace.UP, 1).getType() == Material.WATER_LILY) {
                    pad.put(block.getRelative(BlockFace.UP, 1).getLocation(), block.getRelative(BlockFace.UP, 1).getData());
                    block.getRelative(BlockFace.UP, 1).setType(Material.AIR);
                }
            }
        }

    }

    public static class BlockRestoreMap {

        private BlockRestoreManager _blockRestore;
        // The rate at which we restore blocks
        private int _blocksPerTick;
        // Easy access to all the blocks we have modified
        private HashSet<Block> _changedBlocks;
        // A hashmap for each level, so we can quickly restore top down
        private HashMap<Block, BlockData>[] _blocks;

        protected BlockRestoreMap(BlockRestoreManager blockRestore) {
            this(blockRestore, 50);
        }

        protected BlockRestoreMap(BlockRestoreManager blockRestore, int blocksPerTick) {
            _blockRestore = blockRestore;
            _blocksPerTick = blocksPerTick;
            _changedBlocks = new HashSet<Block>();
            _blocks = new HashMap[256];

            // Populate Array
            for (int i = 0; i < 256; i++) {
                _blocks[i] = new HashMap<Block, BlockData>();
            }
        }

        public void addBlockData(BlockData blockData) {
            Block block = blockData.Block;

            if (block.getY() > 0 && block.getY() < _blocks.length) {
                if (!_blocks[block.getY()].containsKey(block)) {
                    _blocks[block.getY()].put(block, blockData);
                }
            }

            _changedBlocks.add(blockData.Block);
        }

        public void set(Block block, Material material) {
            set(block, material, (byte) 0);
        }

        public void set(Block block, Material material, byte toData) {
            set(block, material.getId(), toData);
        }

        public void set(Block block, int toId, byte toData) {
            addBlockData(new BlockData(block));

            block.setTypeIdAndData(toId, toData, false);
        }

        public boolean contains(Block block) {
            return _changedBlocks.contains(block);
        }

        public HashSet<Block> getChangedBlocks() {
            return _changedBlocks;
        }

        /**
         * Restore all the blocks changed in this BlockRestoreMap
         * NOTE: You should not use the same BlockRestoreMap instance after you run restore.
         * You must initialize a new BlockRestoreMap from BlockRestore
         */
        public void restore() {
            // The idea behind this is that the runnable will restore blocks over time
            // If the server happens to shutdown while the runnable is running, we will still
            // restore all our blocks with restoreInstant (as called by BlockRestore)
            BlockDataRunnable runnable = new BlockDataRunnable(_blockRestore.plugin, new RestoreIterator(), _blocksPerTick, new Runnable() {
                @Override
                public void run() {
                    clearMaps();
                    _blockRestore.removeMap(BlockRestoreMap.this);
                }
            });
            runnable.start();
        }

        private void clearMaps() {
            for (int i = 0; i < 256; i++) {
                _blocks[i].clear();
            }

            _changedBlocks.clear();
        }

        public void restoreInstant() {
            for (int i = 0; i < 256; i++) {
                HashMap<Block, BlockData> map = _blocks[i];
                for (BlockData data : map.values()) {
                    data.restore();
                }
            }

            clearMaps();
        }

        public int getBlocksPerTick() {
            return _blocksPerTick;
        }

        public void setBlocksPerTick(int blocksPerTick) {
            _blocksPerTick = blocksPerTick;
        }

        private class RestoreIterator implements Iterator<BlockData> {
            private Iterator<BlockData> _currentIterator;
            private int _currentIndex;

            public RestoreIterator() {
                _currentIndex = 255;
                updateIterator();
            }

            private void updateIterator() {
                _currentIterator = _blocks[_currentIndex].values().iterator();
            }

            @Override
            public boolean hasNext() {
                while (!_currentIterator.hasNext() && _currentIndex > 0) {
                    _currentIndex--;
                    updateIterator();
                }

                return _currentIterator.hasNext();
            }

            @Override
            public BlockData next() {
                while (!_currentIterator.hasNext() && _currentIndex > 0) {
                    _currentIndex--;
                    updateIterator();
                }

                return _currentIterator.next();
            }

            @Override
            public void remove() {
                _currentIterator.remove();
            }
        }
    }

    public static class BlockDataRunnable implements Runnable {

        private JavaPlugin _plugin;
        private boolean _started;
        private BukkitTask _task;
        private List<BlockData> _changedBlocks;
        private Runnable _onComplete;
        private int _blocksPerTick;
        private Iterator<BlockData> _blockIterator;

        public BlockDataRunnable(JavaPlugin plugin, Iterator<BlockData> blockIterator, int blocksPerTick, Runnable onComplete) {
            _plugin = plugin;
            _changedBlocks = new ArrayList<BlockData>();
            _started = false;
            _blocksPerTick = blocksPerTick;
            _onComplete = onComplete;
            _blockIterator = blockIterator;
        }

        public void start() {
            if (!_started) {
                _task = Bukkit.getScheduler().runTaskTimer(_plugin, this, 1, 1);
                _started = true;
            }
        }

        public void pause() {
            if (_started) {
                _task.cancel();
                _started = false;
            }
        }

        public void setBlocksPerTick(int blocksPerTick) {
            _blocksPerTick = blocksPerTick;
        }

        @Override
        public void run() {
            for (int i = 0; i < _blocksPerTick; i++) {
                if (_blockIterator.hasNext()) {
                    BlockData data = _blockIterator.next();
                    data.restore();
                } else {
                    // We are done
                    _task.cancel();
                    _onComplete.run();
                    return;
                }
            }
        }

    }

    public static class BlockData {
        public Block Block;
        public Material Material;
        public byte Data;
        public long Time;

        public BlockData(Block block) {
            Block = block;
            Material = block.getType();
            Data = block.getData();
            Time = System.currentTimeMillis();
        }

        public void restore() {
            restore(false);
        }

        public void restore(boolean requireNotAir) {
            if (requireNotAir && Block.getType() == org.bukkit.Material.AIR)
                return;

            Block.setTypeIdAndData(Material.getId(), Data, true);
        }
    }

}
