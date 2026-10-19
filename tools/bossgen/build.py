"""Erzeugt StageBossModels.java, Texturen und Vorschauen aus specs.py."""
import sys, os, re
from model import pack, faces, java_layer
from paint import paint
from preview import sheet
import specs

ROOT = '/home/user/Modernized-Space/src/main/'
TEX = ROOT + 'resources/assets/space_m/textures/entity/stage/'
JAVA = ROOT + 'java/com/space_m/entity/mob/stage/client/StageBossModels.java'
PREV = os.path.dirname(os.path.abspath(__file__)) + '/prev/'
os.makedirs(PREV, exist_ok=True)

def build(only=None, write=True):
    blocks = []
    for name, spec in specs.BOSSES.items():
        roots = spec['roots']
        uv = pack(roots, spec['tex'])
        tex, glow = paint(roots, uv, spec['tex'], faces)
        if only is None or name in only:
            poses = []
            for k in sorted(spec.get('poses', {})):
                keys = spec['poses'][k]
                # Pose bei der Haelfte bzw. beim Hoehepunkt (zweiter Schluessel jeder Bahn)
                pt = {}
                for t, bone, rx, ry, rz in keys:
                    if bone not in pt or t > 0 and pt[bone][0] == 0: pt[bone] = (t, (rx, ry, rz))
                poses.append({b: v[1] for b, v in pt.items()})
            sheet(roots, uv, tex, glow, PREV + name + '.png', poses=poses, scale=spec.get('pscale', 5))
        if write:
            tex.save(TEX + name + '.png'); glow.save(TEX + name + '_glow.png')
        code, names = java_layer(spec['java'], roots, uv, spec['tex'], spec['doc'] + ' Textur {@code entity/stage/%s.png}.' % name)
        roles = []
        for r in roots:
            for b, path in r.walk():
                if b.role: roles.append('new Role("%s", "%s", %sF)' % ('/'.join(path), b.role, ('%.2f' % b.param)))
        keys = []
        for anim, ks in sorted(spec.get('poses', {}).items()):
            for t, bone, rx, ry, rz in ks:
                path = next('/'.join(p) for r in roots for b, p in r.walk() if b.name == bone)
                keys.append('new Key(%d, %sF, "%s", %sF, %sF, %sF)' % (anim, t, path, rx, ry, rz))
        const = re.sub(r'(?<!^)(?=[A-Z])', '_', spec['java']).upper()
        blocks.append(code + '\n\n    public static final Role[] %s_ROLES = {%s};\n\n    public static final Key[] %s_POSES = {\n            %s};\n\n    public static final float %s_SCALE = %sF;'
                      % (const, ', '.join(roles), const, ',\n            '.join(keys), const, spec['scale']))
    if write:
        src = '''package com.space_m.entity.mob.stage.client;

import com.space_m.entity.mob.stage.client.StageBossModel.Key;
import com.space_m.entity.mob.stage.client.StageBossModel.Role;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Modelle der Stufen-Bosse (generiert von {@code scratchpad/bossgen} - Knochen, Wuerfel, Materialien, UV-Zuschnitt,
 * Texturen und Posen kommen aus derselben Spezifikation; nicht von Hand pflegen). Je Boss: {@link LayerDefinition},
 * Rollen fuer die Grundbewegung, Schluesselposen je Angriff (Index wie in {@code Bosses}, 0 = ruhend) und Groesse.
 */
public final class StageBossModels {

    private StageBossModels() {}

    private static final CubeDeformation NONE = CubeDeformation.NONE;

''' + '\n\n'.join(blocks) + '\n}\n'
        open(JAVA, 'w').write(src)
        regs = []
        for name, spec in specs.BOSSES.items():
            const = re.sub(r'(?<!^)(?=[A-Z])', '_', spec['java']).upper()
            regs.append('        EntityRendererRegistry.register(StageBosses.%s, ctx -> new RenderStageBoss(ctx, "%s", StageBossModels.%s(), StageBossModels.%s_ROLES,\n                StageBossModels.%s_POSES, StageBossModels.%s_SCALE));'
                        % (name.upper(), name, spec['java'], const, const, const))
        client = '''package com.space_m.entity.mob.stage.client;

import com.space_m.entity.mob.stage.StageBosses;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

/** Renderer der Stufen-Bosse und ihres Geschosses (aus {@code BossClient}; generiert von {@code scratchpad/bossgen}). */
public final class StageBossClient {

    private StageBossClient() {}

    public static void init() {
''' + '\n'.join(regs) + '''
        EntityRendererRegistry.register(StageBosses.BOSS_ORB, BossOrbRenderer::new);
    }
}
'''
        open(ROOT + 'java/com/space_m/entity/mob/stage/client/StageBossClient.java', 'w').write(client)

if __name__ == '__main__':
    build(only=sys.argv[1:] or None, write='--nowrite' not in sys.argv)
