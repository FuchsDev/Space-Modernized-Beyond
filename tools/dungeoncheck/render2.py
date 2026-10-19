"""Bilder eines geprueften Dungeons (Ausgabe von DCheck). Aufruf:
  render2.py <ordner> iso [ymax]          isometrische Ansicht (Fels/Oberflaeche ausgeblendet; ymax schneidet oben ab)
  render2.py <ordner> slice <y> [y2]       Draufsicht-Schnitt: was zwischen y..y2 (Standard y+3) zu sehen ist, von oben
  render2.py <ordner> top                  Draufsicht auf alles (ohne Fels)
  render2.py <ordner> section u=<n>|v=<n>  senkrechter Schnitt
Markierungen: gruen Eingang, gelb Schluessel, rot Boss, blau Beute, violett Spawner, weiss Mobs; cyan Punkte = begehbar."""
import sys, struct, json, re, os, zlib
import numpy as np
from PIL import Image, ImageDraw
H_ = os.path.dirname(os.path.abspath(__file__))
COL = json.load(open(H_ + '/block_colors.json'))
GLOWK = ('lantern', 'froglight', 'glowstone', 'sea_lantern', 'shroomlight', 'lava', 'magma', 'torch', 'end_rod', 'lamp', 'fire', 'seal_field', 'beacon', 'crying')

def load(d):
    raw = open(d + '/vol.bin', 'rb').read()
    N, H, Y0 = struct.unpack('>iii', raw[:12])
    ids = np.frombuffer(raw[12:], dtype='>u2').astype(np.int32).reshape(N, N, H)
    names = [l.strip() for l in open(d + '/palette.txt')]
    cols = np.zeros((len(names), 3), np.float32); glow = np.zeros(len(names), bool); hide = np.zeros(len(names), bool)
    for i, n in enumerate(names):
        b = re.sub(r'\[.*', '', n)
        if b in ('minecraft:air', 'minecraft:cave_air', 'harness:rock', 'harness:turf'): hide[i] = True
        c = COL.get(b)
        if c is None:
            if b == 'space_m:seal_field': c = (200, 80, 255)
            elif b == 'space_m:seal_lock': c = (40, 20, 60)
            else:
                h = zlib.crc32(b.encode()); c = (90 + h % 120, 90 + (h >> 8) % 120, 90 + (h >> 16) % 120)
        cols[i] = c; glow[i] = any(k in b for k in GLOWK)
    marks = [l.split() for l in open(d + '/marks.txt')] if os.path.exists(d + '/marks.txt') else []
    return ids, names, cols, glow, hide, N, H, Y0, marks

MC = {'entry': (60, 255, 60), 'key': (255, 220, 0), 'boss': (255, 40, 40), 'loot': (60, 140, 255), 'spawner': (190, 60, 255), 'mob': (240, 240, 240)}

def iso(d, ymax=None, a=3):
    ids, names, cols, glow, hide, N, H, Y0, marks = load(d)
    vis = ~hide[ids]
    if ymax is not None: vis[:, :, max(0, ymax - Y0 + 1):] = False
    # nur Bloecke mit sichtbarer Seite (oben, Ost, Sued: Kamera bei +x, +y, +z)
    pad = np.pad(vis, 1, constant_values=False)
    exp = ~pad[2:, 1:-1, 1:-1] | ~pad[1:-1, 2:, 1:-1] | ~pad[1:-1, 1:-1, 2:]
    vis = vis & exp
    X, Z, Y = ids.shape
    W = (X + Z) * a + 4; Hh = (X + Z) * a // 2 + Y * a + 4
    img = np.zeros((Hh, W, 3), np.float32) + (18, 16, 24); dep = np.full((Hh, W), -1e9)
    xs, zs, ys = np.nonzero(vis)
    order = np.argsort(xs + zs + ys)
    xs, zs, ys = xs[order], zs[order], ys[order]
    c = cols[ids[xs, zs, ys]] * 1.0
    # einfache Schattierung: Hoehe + Seiten
    shade = 0.75 + 0.25 * (ys / max(1, Y))
    c = c * shade[:, None]
    g = glow[ids[xs, zs, ys]]; c[g] = np.minimum(255, c[g] * 1.25 + 30)
    px = (xs - zs) * a + Z * a + 2; py = (xs + zs) * a // 2 + (Y - ys) * a + 2
    for k in range(len(xs)):
        y0, x0 = py[k], px[k]
        img[y0:y0 + a, x0:x0 + 2 * a] = c[k] * 0.82
        img[y0:y0 + a // 2 + 1, x0:x0 + 2 * a] = np.minimum(255, c[k] * 1.08)
    Image.fromarray(img.clip(0, 255).astype(np.uint8)).save(d + '/iso.png')
    print(d + '/iso.png')

def topview(d, y0=None, y1=None, name='top', scale=4):
    ids, names, cols, glow, hide, N, H, Y0, marks = load(d)
    R = (N - 1) // 2
    lo = 0 if y0 is None else max(0, y0 - Y0); hi = H - 1 if y1 is None else min(H - 1, y1 - Y0)
    sub = ids[:, :, lo:hi + 1]; vis = ~hide[sub]
    any_ = vis.any(axis=2)
    top = np.where(any_, vis.shape[2] - 1 - np.argmax(vis[:, :, ::-1], axis=2), 0)
    blk = np.take_along_axis(sub, top[:, :, None], 2)[:, :, 0]
    c = cols[blk] * (0.55 + 0.45 * (top / max(1, hi - lo)))[:, :, None]
    c[~any_] = (14, 12, 20)
    img = Image.fromarray(np.transpose(c, (1, 0, 2)).clip(0, 255).astype(np.uint8)).resize((N * scale, N * scale), Image.NEAREST)
    dr = ImageDraw.Draw(img)
    if os.path.exists(d + '/reach.bin') and y0 is not None:
        raw = open(d + '/reach.bin', 'rb').read(); n = struct.unpack('>i', raw[:4])[0]
        ks = np.frombuffer(raw[4:4 + 8 * n], dtype='>i8')
        u = (ks >> 40) - 1000; y = ((ks >> 20) & 0xFFFFF) - 1000; v = (ks & 0xFFFFF) - 1000
        m = (y >= y0) & (y <= y1)
        for uu, vv in zip(u[m], v[m]):
            cx, cy = (uu + R) * scale + scale // 2, (vv + R) * scale + scale // 2
            dr.point((cx, cy), fill=(80, 255, 255))
    for mk in marks:
        t, u, y, v = mk[0], int(mk[1]), int(mk[2]), int(mk[3])
        if y0 is not None and not (y0 - 2 <= y <= y1 + 2): continue
        cx, cy = (u + R) * scale + scale // 2, (v + R) * scale + scale // 2
        r = 5 if t in ('boss', 'key', 'entry') else 3
        dr.ellipse((cx - r, cy - r, cx + r, cy + r), outline=MC.get(t, (255, 255, 255)), width=2)
    dr.text((4, 4), 'N oben, Ost rechts; Raster 8: Linien alle 16', fill=(200, 200, 200))
    for g in range(-R, R + 1, 16):
        dr.line(((g + R) * scale, 0, (g + R) * scale, 6), fill=(120, 120, 120)); dr.line((0, (g + R) * scale, 6, (g + R) * scale), fill=(120, 120, 120))
    out = d + '/' + name + '.png'; img.save(out); print(out)

def section(d, axis, at, scale=4):
    ids, names, cols, glow, hide, N, H, Y0, marks = load(d)
    R = (N - 1) // 2
    sl = ids[at + R, :, :] if axis == 'u' else ids[:, at + R, :]   # [w][y]
    c = cols[sl].copy(); c[hide[sl]] = (14, 12, 20)
    rock = np.array([n == 'harness:rock' for n in names])[sl]; c[rock] = (40, 36, 34)
    img = Image.fromarray(np.transpose(c, (1, 0, 2))[::-1].clip(0, 255).astype(np.uint8)).resize((N * scale, H * scale), Image.NEAREST)
    dr = ImageDraw.Draw(img)
    for mk in marks:
        t, u, y, v = mk[0], int(mk[1]), int(mk[2]), int(mk[3])
        if (axis == 'u' and abs(u - at) > 2) or (axis == 'v' and abs(v - at) > 2): continue
        w = v if axis == 'u' else u
        cx, cy = (w + R) * scale + scale // 2, (H - 1 - (y - Y0)) * scale + scale // 2
        dr.ellipse((cx - 4, cy - 4, cx + 4, cy + 4), outline=MC.get(t, (255, 255, 255)), width=2)
    dr.text((4, 4), '%s=%d  (y %d..%d, %s nach rechts)' % (axis, at, Y0, Y0 + H - 1, 'Sued' if axis == 'u' else 'Ost'), fill=(220, 220, 220))
    out = d + '/section_%s%d.png' % (axis, at); img.save(out); print(out)

if __name__ == '__main__':
    d, mode = sys.argv[1], sys.argv[2]
    if mode == 'iso': iso(d, int(sys.argv[3]) if len(sys.argv) > 3 else None)
    elif mode == 'top': topview(d)
    elif mode == 'slice':
        y = int(sys.argv[3]); y2 = int(sys.argv[4]) if len(sys.argv) > 4 else y + 3
        topview(d, y, y2, name='slice_%d_%d' % (y, y2))
    elif mode == 'section':
        ax, n = sys.argv[3].split('='); section(d, ax, int(n))
