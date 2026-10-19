"""Modell-Baukasten (Blockbench-artig): Knochen mit Pivot/Rotation, Wuerfel mit Material; Box-UV-Packen;
Java-Export (LayerDefinition) und Vorschau-Transformation (wie ModelPart: translate pivot, rotate ZYX)."""
import math
import numpy as np

class Cube:
    def __init__(s, x, y, z, w, h, d, mat, infl=0.0):
        s.x, s.y, s.z, s.w, s.h, s.d, s.mat, s.infl = x, y, z, int(round(w)), int(round(h)), int(round(d)), mat, infl
        assert s.w >= 1 and s.h >= 1 and s.d >= 1, (w, h, d)

class Bone:
    def __init__(s, name, pivot=(0, 0, 0), rot=(0, 0, 0), cubes=(), children=(), role=None, param=0.0):
        s.name = name; s.pivot = tuple(pivot); s.rot = tuple(math.radians(r) for r in rot)
        s.cubes = list(cubes); s.children = list(children); s.role = role; s.param = param
    def add(s, *bones):
        s.children += bones; return s
    def walk(s, path=()):
        p = path + (s.name,)
        yield s, p
        for c in s.children: yield from c.walk(p)

def C(*a, **k): return Cube(*a, **k)
def B(*a, **k): return Bone(*a, **k)

def footprint(c):
    return 2 * (c.d + c.w), c.d + c.h

def pack(roots, size):
    items = []
    for r in roots:
        for b, _ in r.walk():
            for i, c in enumerate(b.cubes): items.append((b, i, c))
    items.sort(key=lambda t: (-footprint(t[2])[1], -footprint(t[2])[0]))
    # Regal-Packen
    x = y = row = 0; uv = {}
    for b, i, c in items:
        fw, fh = footprint(c)
        if x + fw > size: x = 0; y += row; row = 0
        if y + fh > size: raise Exception('Textur zu klein (%d) bei %s' % (size, b.name))
        uv[(id(b), i)] = (x, y); x += fw; row = max(row, fh)
    return uv

def faces(u, v, c):
    w, h, d = c.w, c.h, c.d
    return {'top': (u + d, v, w, d), 'bottom': (u + d + w, v, w, d), 'west': (u, v + d, d, h), 'front': (u + d, v + d, w, h),
            'east': (u + d + w, v + d, d, h), 'back': (u + 2 * d + w, v + d, w, h)}

# ── Java ──
def jf(v):
    s = ('%.3f' % v).rstrip('0').rstrip('.')
    if s in ('-0', ''): s = '0'
    return s + 'F'

def java_layer(method, roots, uv, size, javadoc):
    lines = ['    /** %s */' % javadoc, '    public static LayerDefinition %s() {' % method,
             '        MeshDefinition mesh = new MeshDefinition();', '        PartDefinition root = mesh.getRoot();']
    names = []
    def emit(b, parent):
        var = 'p_' + b.name
        cubes = 'CubeListBuilder.create()'
        for i, c in enumerate(b.cubes):
            u, v = uv[(id(b), i)]
            de = 'NONE' if not c.infl else 'new CubeDeformation(%s)' % jf(c.infl)
            cubes += '\n                .texOffs(%d, %d).addBox(%s, %s, %s, %d, %d, %d, %s)' % (u, v, jf(c.x), jf(c.y), jf(c.z), c.w, c.h, c.d, de)
        px, py, pz = b.pivot; rx, ry, rz = b.rot
        lines.append('        PartDefinition %s = %s.addOrReplaceChild("%s", %s,\n                PartPose.offsetAndRotation(%s, %s, %s, %s, %s, %s));'
                     % (var, parent, b.name, cubes, jf(px), jf(py), jf(pz), jf(rx), jf(ry), jf(rz)))
        names.append(b.name)
        for ch in b.children: emit(ch, var)
    for r in roots: emit(r, 'root')
    lines.append('        return LayerDefinition.create(mesh, %d, %d);' % (size, size))
    lines.append('    }')
    return '\n'.join(lines), names

# ── Transformation fuer die Vorschau ──
def rotm(rx, ry, rz):
    cx, sx, cy, sy, cz, sz = math.cos(rx), math.sin(rx), math.cos(ry), math.sin(ry), math.cos(rz), math.sin(rz)
    Rx = np.array([[1, 0, 0], [0, cx, -sx], [0, sx, cx]]); Ry = np.array([[cy, 0, sy], [0, 1, 0], [-sy, 0, cy]]); Rz = np.array([[cz, -sz, 0], [sz, cz, 0], [0, 0, 1]])
    return Rz @ Ry @ Rx

def world_cubes(roots, pose=None):
    """Liste (Ecken 8x3 in Modellraum, Wuerfel, Knochen). pose: {bone: (rx, ry, rz) Grad zusaetzlich}."""
    out = []
    def rec(b, M, T):
        rx, ry, rz = b.rot
        if pose and b.name in pose:
            ax, ay, az = pose[b.name]; rx += math.radians(ax); ry += math.radians(ay); rz += math.radians(az)
        T2 = T + M @ np.array(b.pivot, float)
        M2 = M @ rotm(rx, ry, rz)
        for c in b.cubes:
            e = c.infl
            x0, y0, z0 = c.x - e, c.y - e, c.z - e
            x1, y1, z1 = c.x + c.w + e, c.y + c.h + e, c.z + c.d + e
            v = np.array([[x0, y0, z0], [x1, y0, z0], [x1, y1, z0], [x0, y1, z0], [x0, y0, z1], [x1, y0, z1], [x1, y1, z1], [x0, y1, z1]], float)
            out.append(((M2 @ v.T).T + T2, c, b))
        for ch in b.children: rec(ch, M2, T2)
    for r in roots: rec(r, np.eye(3), np.zeros(3))
    return out
