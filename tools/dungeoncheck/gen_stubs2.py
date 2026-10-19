"""Stubs der Minecraft-/Mod-Klassen fuer die Dungeon-Harness (aus den Quellen gelesen) + Pruefung der Blocknamen.
Aufruf: gen_stubs2.py <out> <quelle.java>...   Ausgabe: Fehlerliste unbekannter Bloecke (Exit 3 bei Fehlern)."""
import re, os, sys, glob
H = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.normpath(os.path.join(H, '..', '..', 'src', 'main', 'java')) + '/'
out = sys.argv[1]
src = '\n'.join(open(p).read() for p in sys.argv[2:])
code = re.sub(r'//[^\n]*|/\*.*?\*/', '', src, flags=re.S)   # Kommentare raus
def w(path, text):
    p = os.path.join(out, path); os.makedirs(os.path.dirname(p), exist_ok=True); open(p, 'w').write(text)
errors = []
vanilla = set(open(H + '/vanilla_blocks.txt').read().split())
hbm = set(open(H + '/hbm_modblocks_fields.txt').read().split())
blocks = sorted(set(re.findall(r'\bBlocks\.([A-Z_0-9]+)', code)) | {'AIR', 'CHEST', 'BARREL', 'SPAWNER', 'DISPENSER', 'LADDER', 'CRYING_OBSIDIAN'})
for b in blocks:
    if b.lower() not in vanilla: errors.append('Vanilla-Block gibt es nicht in 1.20.1 UND 1.21.1: Blocks.' + b)
mb = sorted(set(re.findall(r'ModBlocks\.([A-Z_0-9]+)', code)))
for b in mb:
    if b not in hbm: errors.append('HBM-Block unbekannt: ModBlocks.' + b)
props = sorted(set(re.findall(r'BlockStateProperties\.([A-Z_0-9]+)', code)))
fq = {}
for cls, name in re.findall(r'(com\.space_m\.[a-z_.]+\.[A-Z]\w+)\.([A-Z][A-Z_0-9]+)\.get\(\)', code):
    fq.setdefault(cls, set()).add(name)
for cls, names in fq.items():
    f = REPO + cls.replace('.', '/') + '.java'
    if not os.path.exists(f):
        errors.append('Klasse fehlt: ' + cls); continue
    s = open(f).read()
    for n in names:
        if not re.search(r'static final [^=;]*\b%s\s*=' % n, s): errors.append('Feld fehlt: %s.%s' % (cls, n))
# Nur erlaubte Imports in Designs
for imp in re.findall(r'^import\s+([\w.]+);', src, flags=re.M):
    ok = imp.startswith(('com.hbm_m.block.ModBlocks', 'com.space_m.dim.dungeon.', 'com.space_m.progression.SpaceProgression', 'com.space_m.dim.BiomeGenBaseCelestial',
                         'com.space_m.dim.CelestialWorld', 'java.util.', 'net.minecraft.core.Direction', 'net.minecraft.world.level.block.Block',
                         'net.minecraft.world.level.block.Blocks', 'net.minecraft.world.level.block.state.BlockState',
                         'net.minecraft.world.level.block.state.properties.', 'com.space_m.dim.sporos.city.CityPalette', 'com.space_m.dim.sporos.city.CityRandom'))
    if not ok: errors.append('Import nicht erlaubt (Harness/Stubs): ' + imp)
enums = {}
for cls, c in re.findall(r'\b(Half|StairsShape|AttachFace|DoubleBlockHalf|BedPart|ChestType|WallSide|RedstoneSide|BellAttachType|Tilt|DripstoneThickness|RailShape|SlabType|DoorHingeSide)\.([A-Z_]+)\b', code):
    enums.setdefault(cls, set()).add(c)
bio = {}
for cls, name in re.findall(r'(com\.space_m\.[a-z_.]+\.biome\.[A-Z]\w+)\.([a-z]\w*)', code):
    bio.setdefault(cls, set()).add(name)

w('net/minecraft/core/Direction.java', '''package net.minecraft.core;
public enum Direction { DOWN(0,-1,0), UP(0,1,0), NORTH(0,0,-1), SOUTH(0,0,1), WEST(-1,0,0), EAST(1,0,0);
  final int x,y,z; Direction(int x,int y,int z){this.x=x;this.y=y;this.z=z;}
  public int getStepX(){return x;} public int getStepY(){return y;} public int getStepZ(){return z;}
  public Direction getOpposite(){ switch(this){case DOWN:return UP;case UP:return DOWN;case NORTH:return SOUTH;case SOUTH:return NORTH;case WEST:return EAST;default:return WEST;} }
  public Direction getClockWise(){ switch(this){case NORTH:return EAST;case EAST:return SOUTH;case SOUTH:return WEST;case WEST:return NORTH;default:throw new IllegalStateException();} }
  public Direction getCounterClockWise(){ return getClockWise().getOpposite(); }
  public Axis getAxis(){ return x!=0?Axis.X:y!=0?Axis.Y:Axis.Z; }
  public static Direction from2DDataValue(int i){ return new Direction[]{SOUTH,WEST,NORTH,EAST}[Math.floorMod(i,4)]; }
  public int get2DDataValue(){ switch(this){case SOUTH:return 0;case WEST:return 1;case NORTH:return 2;case EAST:return 3;default:return -1;} }
  public enum Axis { X, Y, Z }
  public enum Plane { HORIZONTAL, VERTICAL }
}
''')
w('net/minecraft/world/level/block/Block.java', '''package net.minecraft.world.level.block;
import net.minecraft.world.level.block.state.BlockState;
public class Block { public final String name; private final BlockState def;
  public Block(String n){name=n; def=new BlockState(this, new java.util.TreeMap<>());}
  public BlockState defaultBlockState(){return def;} public String toString(){return name;} }
''')
w('net/minecraft/world/level/block/Blocks.java', 'package net.minecraft.world.level.block;\npublic final class Blocks {\n' + ''.join(f'  public static final Block {b} = new Block("minecraft:{b.lower()}");\n' for b in blocks) + '}\n')
w('net/minecraft/world/level/block/state/BlockState.java', '''package net.minecraft.world.level.block.state;
import net.minecraft.world.level.block.Block; import java.util.*;
public final class BlockState { final Block block; public final TreeMap<String,String> props; private String key;
  public BlockState(Block b, TreeMap<String,String> p){block=b;props=p;}
  public Block getBlock(){return block;}
  public boolean isAir(){return block.name.equals("minecraft:air");}
  public boolean is(Block b){return block==b;}
  public BlockState setValue(Object k, Object v){ return with(String.valueOf(k), v); }
  public BlockState with(String k, Object v){ TreeMap<String,String> m=new TreeMap<>(props); m.put(k,String.valueOf(v).toLowerCase()); return new BlockState(block,m);}
  public String key(){ if(key!=null)return key; StringBuilder s=new StringBuilder(block.name); if(!props.isEmpty()){s.append('[');boolean f=true;for(var e:props.entrySet()){if(!f)s.append(',');f=false;s.append(e.getKey()).append('=').append(e.getValue());}s.append(']');} return key=s.toString();}
  public String toString(){return key();}
}
''')
w('net/minecraft/world/level/block/state/properties/BlockStateProperties.java', 'package net.minecraft.world.level.block.state.properties;\npublic final class BlockStateProperties {\n' + ''.join(f'  public static final String {p} = "{p.lower().replace("horizontal_facing","facing").replace("vertical_direction","vertical_direction")}";\n' for p in props) + '}\n')
enums.setdefault('SlabType', set()).update({'TOP', 'BOTTOM', 'DOUBLE'})
for cls, cs in enums.items():
    w(f'net/minecraft/world/level/block/state/properties/{cls}.java', f'package net.minecraft.world.level.block.state.properties;\npublic enum {cls} {{ ' + ', '.join(sorted(cs)) + ' }\n')
w('com/space_m/dim/sporos/city/CityPalette.java', '''package com.space_m.dim.sporos.city;
import net.minecraft.core.Direction; import net.minecraft.world.level.block.Block; import net.minecraft.world.level.block.state.BlockState; import net.minecraft.world.level.block.state.properties.SlabType;
public final class CityPalette {
  public static BlockState slab(BlockState s, SlabType t){return s.with("type",t);}
  public static BlockState with(BlockState s, Object p, Object v){return s.with(String.valueOf(p),v);}
  public static BlockState stairs(Block b, Direction up, boolean top){return b.defaultBlockState().with("facing",up).with("half", top?"top":"bottom");}
  public static BlockState log(BlockState s, Direction.Axis a){return s.with("axis",a);}
  public static BlockState facing(BlockState s, Direction d){return s.with("facing",d);}
  public static BlockState facingAll(BlockState s, Direction d){return s.with("facing",d);}
}
''')
w('com/space_m/dim/sporos/city/CityRandom.java', '''package com.space_m.dim.sporos.city;
public final class CityRandom { public static double unit(long seed, int u, int y, int v) { long h = seed; h = h * 6364136223846793005L + u * 341873128712L + y * 132897987541L + v * 2654435761L; h ^= h >>> 33; h *= 0xff51afd7ed558ccdL; h ^= h >>> 33; h *= 0xc4ceb9fe1a85ec53L; h ^= h >>> 33; return (h >>> 11) * 0x1.0p-53; }
  public static double noise(long seed, int x, int z, int scale) { int gx = Math.floorDiv(x, scale), gz = Math.floorDiv(z, scale); double fx = (x - gx * scale) / (double) scale, fz = (z - gz * scale) / (double) scale; fx = fx * fx * (3 - 2 * fx); fz = fz * fz * (3 - 2 * fz); double a = unit(seed, gx, 0, gz), b = unit(seed, gx + 1, 0, gz); double c = unit(seed, gx, 0, gz + 1), d = unit(seed, gx + 1, 0, gz + 1); return (a + (b - a) * fx) * (1 - fz) + (c + (d - c) * fx) * fz; }
  public static double smoothstep(double lo, double hi, double v) { if (v <= lo) return 0; if (v >= hi) return 1; double t = (v - lo) / (hi - lo); return t * t * (3 - 2 * t); }
}
''')
w('com/space_m/dim/sporos/city/SporosCities.java', 'package com.space_m.dim.sporos.city;\npublic final class SporosCities { public static Object cityAt(long s, int x, int z){return null;} }\n')
sup = 'public static final class S { final net.minecraft.world.level.block.Block b; S(String n){b=new net.minecraft.world.level.block.Block(n);} public net.minecraft.world.level.block.Block get(){return b;} }\n'
w('com/hbm_m/block/ModBlocks.java', 'package com.hbm_m.block;\npublic final class ModBlocks {\n' + sup + ''.join(f'  public static final S {b} = new S("hbm_m:{b.lower()}");\n' for b in mb) + '}\n')
for cls, names in fq.items():
    pkg, name = cls.rsplit('.', 1)
    w(cls.replace('.', '/') + '.java', f'package {pkg};\npublic final class {name} {{\n' + sup + ''.join(f'  public static final S {n} = new S("space_m:{n.lower()}");\n' for n in sorted(names)) + '}\n')
for cls, names in bio.items():
    pkg, name = cls.rsplit('.', 1)
    w(cls.replace('.', '/') + '.java', f'package {pkg};\npublic final class {name} {{\n' + ''.join(f'  public static final com.space_m.dim.BiomeGenBaseCelestial {n} = new com.space_m.dim.BiomeGenBaseCelestial();\n' for n in sorted(names)) + '}\n')
w('com/space_m/dim/BiomeGenBaseCelestial.java', 'package com.space_m.dim;\npublic class BiomeGenBaseCelestial {}\n')
w('com/space_m/dim/CelestialWorld.java', 'package com.space_m.dim;\npublic interface CelestialWorld { BiomeGenBaseCelestial getBiomeGenForCoords(int x, int z); long getSeed(); }\n')
w('com/space_m/progression/SpaceProgression.java', '''package com.space_m.progression;
public final class SpaceProgression { public enum Stage { MUN("mun"), MINMUS("minmus"), DUNA("duna"), IKE("ike"), DRES("dres"), MOHO("moho"), EVE("eve"), LAYTHE("laythe"), TEKTO("tekto"), SPOROS("sporos"), WEMBUROS("wemburos");
  public final String body; Stage(String b){body=b;} public int tier(){return ordinal()+1;} } }
''')
w('com/space_m/dim/dungeon/seal/SealBlocks.java', '''package com.space_m.dim.dungeon.seal;
public final class SealBlocks { public static final class S { final net.minecraft.world.level.block.Block b; S(String n){b=new net.minecraft.world.level.block.Block(n);} public net.minecraft.world.level.block.Block get(){return b;} }
  public static final S SEAL_FIELD = new S("space_m:seal_field"), SEAL_LOCK = new S("space_m:seal_lock"); }
''')
w('com/space_m/dim/dungeon/seal/SealLockBlock.java', 'package com.space_m.dim.dungeon.seal;\npublic final class SealLockBlock { public static final String LIT = "lit"; }\n')
w('com/space_m/dim/dungeon/DungeonDesign.java', '''package com.space_m.dim.dungeon;
import com.space_m.dim.BiomeGenBaseCelestial; import com.space_m.progression.SpaceProgression.Stage; import net.minecraft.world.level.block.state.BlockState;
public interface DungeonDesign { Stage stage(); int radius(); BlockState surface(); BlockState filler();
  default boolean fits(BiomeGenBaseCelestial b){return true;} default boolean fitsAt(com.space_m.dim.CelestialWorld w, int x, int z){return fits(w.getBiomeGenForCoords(x,z));}
  default BlockState outside(int y){return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();}
  default int minBase(){return 50;} default int maxBase(){return 190;} void plan(DungeonPlan p); void paint(DungeonPainter w); default void trace(DungeonOut o){} default int minY(){return -36;} default int maxY(){return 24;} }
''')
w('com/space_m/dim/dungeon/DungeonPainter.java', 'package com.space_m.dim.dungeon;\npublic abstract class DungeonPainter implements DungeonOut {}\n')
w('com/space_m/dim/dungeon/PlanetDungeons.java', '''package com.space_m.dim.dungeon;
public final class PlanetDungeons { public static final int BLEND = 8; public static final java.util.LinkedHashMap<String, DungeonDesign> D = new java.util.LinkedHashMap<>();
  public static void register(String b, DungeonDesign d){D.put(b,d);}
  public record Dungeon(long seed) {} }
''')
w('com/space_m/dim/dungeon/DungeonPlan.java', '''package com.space_m.dim.dungeon;
import net.minecraft.core.Direction; import java.util.*;
public final class DungeonPlan { public record Loot(int u,int y,int v,Direction facing,boolean barrel,String table){} public record Mob(int u,int y,int v,String type){}
  public final PlanetDungeons.Dungeon d; public final int base = 80; public final List<Loot> loot=new ArrayList<>(); public final List<Mob> spawners=new ArrayList<>(), mobs=new ArrayList<>(), elites=new ArrayList<>(), dispensers=new ArrayList<>();
  public DungeonPlan(long seed){d=new PlanetDungeons.Dungeon(seed);}
  public void chest(int u,int y,int v,Direction f,String t){loot.add(new Loot(u,y,v,f,false,t));} public void barrel(int u,int y,int v,String t){loot.add(new Loot(u,y,v,Direction.UP,true,t));}
  public void spawner(int u,int y,int v,String t){spawners.add(new Mob(u,y,v,t));} public void mob(int u,int y,int v,String t){mobs.add(new Mob(u,y,v,t));}
  public void elite(int u,int y,int v,String t){elites.add(new Mob(u,y,v,t));} public void dispenser(int u,int y,int v){dispensers.add(new Mob(u,y,v,"arrow"));} }
''')
if errors:
    print('\n'.join('FEHLER ' + e for e in errors)); sys.exit(3)
