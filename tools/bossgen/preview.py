"""Vorschau texturierter Wuerfelmodelle (orthografisch, Z-Puffer, Lambert), UV wie Vanilla-ModelPart."""
import math, numpy as np
from PIL import Image
from model import world_cubes

# Vanilla-Polygone: (Eckindizes, u1, v1, u2, v2) mit f4..f12
def polys(u, v, c):
    w, h, d = c.w, c.h, c.d
    f4, f5, f6, f7, f8, f9 = u, u + d, u + d + w, u + d + w + w, u + d + w + d, u + d + w + d + w
    f10, f11, f12 = v, v + d, v + d + h
    return [((5, 1, 2, 6), f6, f11, f8, f12),   # EAST +x
            ((0, 4, 7, 3), f4, f11, f5, f12),   # WEST -x
            ((5, 4, 0, 1), f5, f10, f6, f11),   # DOWN (minY, sichtbar oben)
            ((2, 3, 7, 6), f6, f11, f7, f10),   # UP (maxY, sichtbar unten)
            ((1, 0, 3, 2), f5, f11, f6, f12),   # NORTH -z (vorn)
            ((4, 5, 6, 7), f8, f11, f9, f12)]   # SOUTH +z

def render(roots, uv, tex, glow, out, az=35, el=20, scale=6, pose=None, size=None, bg=(24, 22, 30)):
    T = np.asarray(tex.convert('RGBA'), dtype=np.float32); G = np.asarray(glow.convert('RGBA'), dtype=np.float32)
    cubes = world_cubes(roots, pose)
    a, e = math.radians(az), math.radians(el)
    Ry = np.array([[math.cos(a), 0, math.sin(a)], [0, 1, 0], [-math.sin(a), 0, math.cos(a)]])
    Rx = np.array([[1, 0, 0], [0, math.cos(e), -math.sin(e)], [0, math.sin(e), math.cos(e)]])
    V = Rx @ Ry
    tris = []
    allp = []
    for corners, c, b in cubes:
        wpos = corners * np.array([1, -1, -1])           # Welt: (x, -y, -z)
        p = (V @ wpos.T).T
        allp.append(p)
        u0, v0 = uv[(id(b), b.cubes.index(c))]
        for idx, u1, v1, u2, v2 in polys(u0, v0, c):
            q = p[list(idx)]
            uvs = [(u2, v1), (u1, v1), (u1, v2), (u2, v2)]
            n = np.cross(q[1] - q[0], q[2] - q[0])
            nl = np.linalg.norm(n)
            if nl < 1e-9: continue
            n /= nl
            tris.append((q, uvs, n))
    P = np.concatenate(allp)
    mn, mx = P.min(0), P.max(0)
    W = int((mx[0] - mn[0]) * scale) + 20; H = int((mx[1] - mn[1]) * scale) + 20
    img = np.zeros((H, W, 3), np.float32); img[:] = bg
    zb = np.full((H, W), -1e9, np.float32)
    light = np.array([0.3, 0.8, 0.5]); light /= np.linalg.norm(light)
    for q, uvs, n in tris:
        # Kamera schaut entlang -z (Sichtraum); sichtbar, wenn Normale +z
        shade = 0.55 + 0.45 * max(0.0, float(np.dot(n * np.array([1, 1, 1]), (V @ light))))
        for tri in ((0, 1, 2), (0, 2, 3)):
            pts = q[list(tri)]; tuv = np.array([uvs[i] for i in tri], float)
            sx = (pts[:, 0] - mn[0]) * scale + 10; sy = (mx[1] - pts[:, 1]) * scale + 10; sz = pts[:, 2]
            x0, x1 = int(max(0, math.floor(sx.min()))), int(min(W - 1, math.ceil(sx.max())))
            y0, y1 = int(max(0, math.floor(sy.min()))), int(min(H - 1, math.ceil(sy.max())))
            if x1 < x0 or y1 < y0: continue
            gx, gy = np.meshgrid(np.arange(x0, x1 + 1) + 0.5, np.arange(y0, y1 + 1) + 0.5)
            d = (sy[1] - sy[2]) * (sx[0] - sx[2]) + (sx[2] - sx[1]) * (sy[0] - sy[2])
            if abs(d) < 1e-9: continue
            l0 = ((sy[1] - sy[2]) * (gx - sx[2]) + (sx[2] - sx[1]) * (gy - sy[2])) / d
            l1 = ((sy[2] - sy[0]) * (gx - sx[2]) + (sx[0] - sx[2]) * (gy - sy[2])) / d
            l2 = 1 - l0 - l1
            inside = (l0 >= -1e-6) & (l1 >= -1e-6) & (l2 >= -1e-6)
            if not inside.any(): continue
            z = l0 * sz[0] + l1 * sz[1] + l2 * sz[2]
            uu = l0 * tuv[0, 0] + l1 * tuv[1, 0] + l2 * tuv[2, 0]
            vv = l0 * tuv[0, 1] + l1 * tuv[1, 1] + l2 * tuv[2, 1]
            ui = np.clip(np.floor(uu).astype(int), 0, T.shape[1] - 1); vi = np.clip(np.floor(vv).astype(int), 0, T.shape[0] - 1)
            sub_z = zb[y0:y1 + 1, x0:x1 + 1]
            alpha = T[vi, ui, 3]
            m = inside & (z > sub_z) & (alpha > 10)
            if not m.any(): continue
            col = T[vi, ui, :3] * shade
            g = G[vi, ui]
            col = np.where((g[..., 3:4] > 10), g[..., :3], col)
            region = img[y0:y1 + 1, x0:x1 + 1]
            region[m] = col[m]
            sub_z[m] = z[m]
    im = Image.fromarray(np.clip(img, 0, 255).astype(np.uint8))
    if out: im.save(out)
    return im

def sheet(roots, uv, tex, glow, out, poses=(), scale=5):
    """Mehrere Ansichten nebeneinander: vorn-links, vorn-rechts, hinten, plus Posen."""
    ims = [render(roots, uv, tex, glow, None, az=a, el=e, scale=scale) for a, e in ((30, 18), (-40, 22), (160, 25))]
    for p in poses: ims.append(render(roots, uv, tex, glow, None, az=30, el=18, scale=scale, pose=p))
    W = sum(i.width for i in ims) + 10 * len(ims); H = max(i.height for i in ims)
    s = Image.new('RGB', (W, H), (24, 22, 30)); x = 0
    for i in ims: s.paste(i, (x, H - i.height)); x += i.width + 10
    s.save(out)
    return s
