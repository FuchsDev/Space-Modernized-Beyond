"""3D-Waffenmodelle (Item-JSON mit Elementen): Waffe senkrecht entworfen (Laenge entlang +y um die Mitte 8,8,8),
alle Elemente gemeinsam um -45 Grad (z) gekippt -> Diagonale wie ein Schwert-Sprite, Vanilla-Haltung passt.
Texturen mit den Materialien aus paint.py, leuchtende Teile emissiv (forge_data/neoforge_data)."""
import json, math, os, sys, zlib
import numpy as np
from PIL import Image
import paint
from paint import mat, M, paint_face

ROOT = '/home/user/Modernized-Space/src/main/resources/assets/space_m/'
OUT_MODEL = ROOT + 'models/item/'
OUT_TEX = ROOT + 'textures/item/weapon/'
PREV = os.path.dirname(os.path.abspath(__file__)) + '/wprev/'

class E:
    """Teil im Waffenraum: x quer (Klingenbreite), y entlang der Waffe (0 = Mitte), z Dicke; Mittelpunkt (cx, cz)."""
    def __init__(s, x0, y0, z0, x1, y1, z1, m):
        s.f = (x0, y0, z0); s.t = (x1, y1, z1); s.m = m

def box(cx, y0, y1, w, d, m, ox=0.0, oz=0.0):
    return E(cx - w / 2 + ox, y0, -d / 2 + oz, cx + w / 2 + ox, y1, d / 2 + oz, m)

FACES = ['north', 'south', 'east', 'west', 'up', 'down']
LIGHT = {'north': 'front', 'south': 'back', 'east': 'east', 'west': 'west', 'up': 'top', 'down': 'bottom'}

def face_dims(e, f):
    dx, dy, dz = (e.t[i] - e.f[i] for i in range(3))
    return {'north': (dx, dy), 'south': (dx, dy), 'east': (dz, dy), 'west': (dz, dy), 'up': (dx, dz), 'down': (dx, dz)}[f]

def build(name, parts, size=64, long=False, gui=0.9, tilt=True):
    tex = Image.new('RGBA', (size, size), (0, 0, 0, 0)); glow = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    x = y = row = 0
    elements = []
    for i, e in enumerate(parts):
        faces = {}
        for f in FACES:
            w, h = face_dims(e, f)
            pw, ph = max(1, int(math.ceil(w - 1e-6))), max(1, int(math.ceil(h - 1e-6)))
            if x + pw > size: x = 0; y += row; row = 0
            if y + ph > size: raise Exception('Textur zu klein: ' + name)
            m = M[e.m]
            paint_face(tex, glow, (x, y, pw, ph), LIGHT[f], m, zlib.crc32(('%s%d%s' % (name, i, f)).encode()))
            k = 16.0 / size
            faces[f] = {'uv': [round(x * k, 4), round(y * k, 4), round((x + pw) * k, 4), round((y + ph) * k, 4)], 'texture': '#0'}
            x += pw; row = max(row, ph)
        el = {'from': [round(8 + e.f[0], 3), round(8 + e.f[1], 3), round(8 + e.f[2], 3)],
              'to': [round(8 + e.t[0], 3), round(8 + e.t[1], 3), round(8 + e.t[2], 3)],
              'faces': faces}
        if tilt: el['rotation'] = {'angle': -45, 'axis': 'z', 'origin': [8, 8, 8]}
        if M[e.m].glow or M[e.m].kind in ('glow', 'eye'):
            el['forge_data'] = {'block_light': 15, 'sky_light': 15}
            el['neoforge_data'] = {'block_light': 15, 'sky_light': 15}
            el['shade'] = False
        elements.append(el)
    s3 = 0.85 * (0.8 if long else 1.0); s1 = 0.68 * (0.85 if long else 1.0)
    if not tilt:
        disp = {'thirdperson_righthand': {'rotation': [75, 45, 0], 'translation': [0, 2.5, 0], 'scale': [0.45, 0.45, 0.45]},
                'thirdperson_lefthand': {'rotation': [75, 45, 0], 'translation': [0, 2.5, 0], 'scale': [0.45, 0.45, 0.45]},
                'firstperson_righthand': {'rotation': [0, 45, 0], 'translation': [0, 2, 0], 'scale': [0.5, 0.5, 0.5]},
                'firstperson_lefthand': {'rotation': [0, 225, 0], 'translation': [0, 2, 0], 'scale': [0.5, 0.5, 0.5]},
                'ground': {'translation': [0, 3, 0], 'scale': [0.35, 0.35, 0.35]},
                'gui': {'rotation': [25, 35, 0], 'scale': [gui, gui, gui]},
                'fixed': {'scale': [0.6, 0.6, 0.6]}, 'head': {'translation': [0, 6, 0], 'scale': [0.8, 0.8, 0.8]}}
        return {'credit': 'Space Modernized - generiert von scratchpad/bossgen/weapongen.py', 'texture_size': [size, size],
                'textures': {'0': 'space_m:item/core/' + name, 'particle': 'space_m:item/core/' + name},
                'elements': elements, 'display': disp}, tex, glow
    model = {
        'credit': 'Space Modernized - generiert von scratchpad/bossgen/weapongen.py',
        'texture_size': [size, size],
        'textures': {'0': 'space_m:item/weapon/' + name, 'particle': 'space_m:item/weapon/' + name},
        'gui_light': 'front',
        'elements': elements,
        'display': {
            'thirdperson_righthand': {'rotation': [0, -90, 55], 'translation': [0, 4.0 if not long else 6.0, 0.5], 'scale': [s3, s3, s3]},
            'thirdperson_lefthand': {'rotation': [0, 90, -55], 'translation': [0, 4.0 if not long else 6.0, 0.5], 'scale': [s3, s3, s3]},
            'firstperson_righthand': {'rotation': [0, -90, 25], 'translation': [1.13, 3.2, 1.13], 'scale': [s1, s1, s1]},
            'firstperson_lefthand': {'rotation': [0, 90, -25], 'translation': [1.13, 3.2, 1.13], 'scale': [s1, s1, s1]},
            'ground': {'translation': [0, 2, 0], 'scale': [0.5, 0.5, 0.5]},
            'gui': {'rotation': [0, 0, 0], 'scale': [gui, gui, gui]},
            'fixed': {'rotation': [0, 180, 0], 'scale': [1, 1, 1]},
            'head': {'rotation': [0, 180, 0], 'translation': [0, 13, 7], 'scale': [1, 1, 1]},
        }}
    return model, tex, glow

def render_preview(model, tex, glow, out, az=0, el=0, px=10):
    """Vorschau: Elemente gekippt, orthografisch von vorn (Sueden) mit leichtem Drehwinkel."""
    T = np.asarray(tex, dtype=np.float32); G = np.asarray(glow, dtype=np.float32); S = T.shape[0]
    a, e = math.radians(az), math.radians(el)
    Ry = np.array([[math.cos(a), 0, math.sin(a)], [0, 1, 0], [-math.sin(a), 0, math.cos(a)]])
    Rx = np.array([[1, 0, 0], [0, math.cos(e), -math.sin(e)], [0, math.sin(e), math.cos(e)]])
    V = Rx @ Ry
    rz = math.radians(-45 if any('rotation' in e for e in model['elements']) else 0); Rz = np.array([[math.cos(rz), -math.sin(rz), 0], [math.sin(rz), math.cos(rz), 0], [0, 0, 1]])
    W = H = int(34 * px); img = np.zeros((H, W, 3), np.float32); img[:] = (30, 28, 36); zb = np.full((H, W), -1e9)
    for el_ in model['elements']:
        f, t = np.array(el_['from']), np.array(el_['to'])
        c = np.array([[f[0], f[1], f[2]], [t[0], f[1], f[2]], [t[0], t[1], f[2]], [f[0], t[1], f[2]], [f[0], f[1], t[2]], [t[0], f[1], t[2]], [t[0], t[1], t[2]], [f[0], t[1], t[2]]])
        c = (Rz @ (c - 8).T).T
        p = (V @ c.T).T
        quads = {'north': (1, 0, 3, 2), 'south': (4, 5, 6, 7), 'east': (5, 1, 2, 6), 'west': (0, 4, 7, 3), 'up': (7, 6, 2, 3), 'down': (0, 1, 5, 4)}
        for fn, idx in quads.items():
            q = p[list(idx)]
            n = np.cross(q[1] - q[0], q[3] - q[0]);
            if np.linalg.norm(n) < 1e-9 or n[2] <= 0: continue
            n = n / np.linalg.norm(n)
            u1, v1, u2, v2 = [v * S / 16 for v in el_['faces'][fn]['uv']]
            # Ecken: 0 -> (u1, v2) unten links, 1 -> (u2, v2), 2 -> (u2, v1), 3 -> (u1, v1)
            uvs = [(u1, v2), (u2, v2), (u2, v1), (u1, v1)]
            shade = 1.0 if el_.get('shade') is False else 0.6 + 0.4 * max(0, n @ np.array([0.3, 0.7, 0.6]) / 1.0)
            for tri in ((0, 1, 2), (0, 2, 3)):
                pts = q[list(tri)]; tuv = np.array([uvs[i] for i in tri])
                sx = pts[:, 0] * px + W / 2; sy = -pts[:, 1] * px + H / 2; sz = pts[:, 2]
                x0, x1 = max(0, int(sx.min())), min(W - 1, int(sx.max()) + 1); y0, y1 = max(0, int(sy.min())), min(H - 1, int(sy.max()) + 1)
                if x1 < x0 or y1 < y0: continue
                gx, gy = np.meshgrid(np.arange(x0, x1 + 1) + 0.5, np.arange(y0, y1 + 1) + 0.5)
                d = (sy[1] - sy[2]) * (sx[0] - sx[2]) + (sx[2] - sx[1]) * (sy[0] - sy[2])
                if abs(d) < 1e-9: continue
                l0 = ((sy[1] - sy[2]) * (gx - sx[2]) + (sx[2] - sx[1]) * (gy - sy[2])) / d
                l1 = ((sy[2] - sy[0]) * (gx - sx[2]) + (sx[0] - sx[2]) * (gy - sy[2])) / d
                l2 = 1 - l0 - l1
                m = (l0 >= -1e-6) & (l1 >= -1e-6) & (l2 >= -1e-6)
                z = l0 * sz[0] + l1 * sz[1] + l2 * sz[2]
                uu = np.clip((l0 * tuv[0, 0] + l1 * tuv[1, 0] + l2 * tuv[2, 0]).astype(int), 0, S - 1)
                vv = np.clip((l0 * tuv[0, 1] + l1 * tuv[1, 1] + l2 * tuv[2, 1]).astype(int), 0, S - 1)
                sub = zb[y0:y1 + 1, x0:x1 + 1]
                m = m & (z > sub) & (T[vv, uu, 3] > 10)
                col = np.where(G[vv, uu, 3:4] > 10, G[vv, uu, :3], T[vv, uu, :3] * shade)
                reg = img[y0:y1 + 1, x0:x1 + 1]; reg[m] = col[m]; sub[m] = z[m]
    Image.fromarray(np.clip(img, 0, 255).astype(np.uint8)).save(out)

def sheet(name, model, tex, glow):
    paths = []
    for i, (az, el) in enumerate(((0, 0), (35, 15), (-60, 20))):
        p = PREV + '%s_%d.png' % (name, i); render_preview(model, tex, glow, p, az, el); paths.append(p)
    ims = [Image.open(p) for p in paths]
    s = Image.new('RGB', (sum(i.width for i in ims), ims[0].height)); x = 0
    for i in ims: s.paste(i, (x, 0)); x += i.width
    big = tex.resize((tex.width * 4, tex.height * 4), Image.NEAREST)
    s2 = Image.new('RGB', (s.width + big.width, max(s.height, big.height)), (30, 28, 36)); s2.paste(s, (0, 0)); s2.paste(big, (s.width, 0))
    s2.save(PREV + name + '.png')

def write(name, model, tex, glow, sub='weapon'):
    out = ROOT + 'textures/item/%s/' % sub
    os.makedirs(out, exist_ok=True)
    json.dump(model, open(OUT_MODEL + name + '.json', 'w'), indent=1)
    tex.save(out + name + '.png')
    return
    os.makedirs(OUT_TEX, exist_ok=True)
    json.dump(model, open(OUT_MODEL + name + '.json', 'w'), indent=1)
    # Leuchtteile sind Teil der Haupttextur (emissiv per forge_data); glow wird nicht gebraucht
    tex.save(OUT_TEX + name + '.png')
