"""Erzeugt DungeonMobModels.java, DungeonMobClient.java, Texturen und Vorschauen aus mob_<name>.py.
  python3 build_mobs.py                 alle bauen und schreiben
  python3 build_mobs.py --nowrite NAME  nur Vorschau fuer NAME (mprev/NAME.png), nichts ins Repo"""
import sys, os, re
from model import pack, faces, java_layer
from paint import paint
from preview import sheet
import mobs

ROOT = '/home/user/Modernized-Space/src/main/'
TEX = ROOT + 'resources/assets/space_m/textures/entity/dungeon/'
JDIR = ROOT + 'java/com/space_m/entity/mob/dungeon/client/'
PREV = os.path.dirname(os.path.abspath(__file__)) + '/mprev/'
os.makedirs(PREV, exist_ok=True)

def const(java): return re.sub(r'(?<!^)(?=[A-Z])', '_', java).upper()

def build(only=None, write=True):
    specs = mobs.load(None if write else only)
    blocks, regs = [], []
    for name in mobs.ORDER:
        if name not in specs: continue
        spec = specs[name]
        roots = spec['roots']
        uv = pack(roots, spec['tex'])
        tex, glow = paint(roots, uv, spec['tex'], faces)
        if only is None or name in only:
            poses = []
            for k in sorted(spec.get('poses', {})):
                pt = {}
                for t, bone, rx, ry, rz in spec['poses'][k]:
                    if bone not in pt or t > 0 and pt[bone][0] == 0: pt[bone] = (t, (rx, ry, rz))
                poses.append({b: v[1] for b, v in pt.items()})
            sheet(roots, uv, tex, glow, PREV + name + '.png', poses=poses, scale=spec.get('pscale', 9))
            print(PREV + name + '.png')
        if write:
            tex.save(TEX + name + '.png'); glow.save(TEX + name + '_glow.png')
        code, _ = java_layer(spec['java'], roots, uv, spec['tex'], spec['doc'] + ' Textur {@code entity/dungeon/%s.png}.' % name)
        roles = []
        for r in roots:
            for b, path in r.walk():
                if b.role: roles.append('new Role("%s", "%s", %sF)' % ('/'.join(path), b.role, ('%.2f' % b.param)))
        keys = []
        for anim, ks in sorted(spec.get('poses', {}).items()):
            for t, bone, rx, ry, rz in ks:
                path = next('/'.join(p) for r in roots for b, p in r.walk() if b.name == bone)
                keys.append('new Key(%d, %sF, "%s", %sF, %sF, %sF)' % (anim, t, path, rx, ry, rz))
        c = const(spec['java'])
        blocks.append(code + '\n\n    public static final Role[] %s_ROLES = {%s};\n\n    public static final Key[] %s_POSES = {\n            %s};\n\n    public static final float %s_SCALE = %sF;'
                      % (c, ', '.join(roles), c, ',\n            '.join(keys), c, spec['scale']))
        regs.append('        EntityRendererRegistry.register(DungeonMobs.TYPES.get("%s"), ctx -> new RenderDungeonMob(ctx, "%s", DungeonMobModels.%s(),\n                DungeonMobModels.%s_ROLES, DungeonMobModels.%s_POSES, DungeonMobModels.%s_SCALE));'
                    % (name, name, spec['java'], c, c, c))
    if not write: return
    open(JDIR + 'DungeonMobModels.java', 'w').write('''package com.space_m.entity.mob.dungeon.client;

import com.space_m.entity.mob.stage.client.StageBossModel.Key;
import com.space_m.entity.mob.stage.client.StageBossModel.Role;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Modelle der Dungeon-Mobs (generiert von {@code tools/bossgen/build_mobs.py} aus {@code mob_<name>.py}; nicht von Hand
 * pflegen). Je Mob: {@link LayerDefinition}, Rollen fuer die Grundbewegung, Schluesselposen (1 Schlag, 2 Schuss,
 * 3 Spezial) und Groesse.
 */
public final class DungeonMobModels {

    private DungeonMobModels() {}

    private static final CubeDeformation NONE = CubeDeformation.NONE;

''' + '\n\n'.join(blocks) + '\n}\n')
    open(JDIR + 'DungeonMobClient.java', 'w').write('''package com.space_m.entity.mob.dungeon.client;

import com.space_m.entity.mob.dungeon.DungeonMobs;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

/** Renderer der Dungeon-Mobs (aus {@code BossClient}; generiert von {@code tools/bossgen/build_mobs.py}). */
public final class DungeonMobClient {

    private DungeonMobClient() {}

    public static void init() {
''' + '\n'.join(regs) + '''
    }
}
''')

if __name__ == '__main__':
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    build(only=args or None, write='--nowrite' not in sys.argv)
