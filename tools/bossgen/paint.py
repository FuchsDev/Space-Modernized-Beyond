"""Materialien der Boss-Texturen: Pixel-Art-Rampen statt Rauschen, Fasen, Naehte, Nieten, Risse, Schuppen, Leuchten."""
import math, random, zlib
from PIL import Image

def cl(v): return max(0, min(255, int(round(v))))
def mix(a, b, t): return tuple(cl(a[i] + (b[i] - a[i]) * t) for i in range(3))
def sh(c, f): return tuple(cl(v * f) for v in c)
def add(c, d): return tuple(cl(v + d) for v in c)

class Mat:
    def __init__(s, kind, base, accent=None, glow=False, glow_col=None):
        s.kind, s.base, s.accent, s.glow, s.glow_col = kind, base, accent or base, glow, glow_col
M = {}
def mat(name, kind, base, accent=None, glow=False, glow_col=None): M[name] = Mat(kind, base, accent, glow, glow_col)

def hsh(*a):
    h = 2166136261
    for x in a: h = ((h ^ (int(x) & 0xffffffff)) * 16777619) & 0xffffffff
    return h / 4294967296.0

def paint_face(tex, glow, rect, face, m, seed):
    fx, fy, fw, fh = rect
    R = random.Random(seed)
    base = m.base
    lightf = {'top': 1.12, 'bottom': 0.72, 'front': 1.0, 'back': 0.9, 'west': 0.94, 'east': 0.94}[face]
    side = face in ('front', 'back', 'west', 'east')
    def put(x, y, c, g=None):
        tex.putpixel((fx + x, fy + y), c + (255,))
        if g is not None: glow.putpixel((fx + x, fy + y), g + (255,))
    k = m.kind
    for y in range(fh):
        for x in range(fw):
            n = hsh(seed, x, y)
            t = y / max(1, fh - 1)                       # 0 oben, 1 unten
            grad = (1.08 - 0.22 * t) if side else 1.0     # Seiten oben heller (AO unten)
            c = sh(base, lightf * grad)
            edge = x == 0 or y == 0 or x == fw - 1 or y == fh - 1
            g = None
            if k == 'plate':
                # Fase: oben/links hell, unten/rechts dunkel; Naehte; Nieten; Kratzer
                if y == 0 or x == 0: c = sh(c, 1.18)
                elif y == fh - 1 or x == fw - 1: c = sh(c, 0.68)
                elif fw >= 8 and x % 8 == 0 or fh >= 8 and y % 8 == 0: c = sh(c, 0.78)
                elif (x in (1, fw - 2)) and (y in (1, fh - 2)) and fw > 4 and fh > 4: c = sh(m.accent, 1.25)
                elif n > 0.985: c = sh(c, 1.25)
                elif n < 0.015: c = sh(c, 0.75)
            elif k == 'frame':
                c = sh(c, 0.92 + 0.12 * (1 if (y % 3) == 0 else 0) - 0.1 * (1 if (y % 3) == 2 else 0))
                if edge: c = sh(c, 0.75)
                if n > 0.97: c = sh(c, 1.2)
            elif k == 'hazard':
                stripe = ((x + y) // 3) % 2 == 0
                c = sh((235, 190, 30) if stripe else (30, 28, 26), lightf * grad)
                if edge: c = sh(c, 0.75)
            elif k == 'ice':
                c = mix(sh(base, lightf * 1.1), sh(m.accent, lightf), t * 0.8)
                if edge: c = sh(c, 1.22)
                if hsh(seed, x - y, 7) > 0.93: c = sh(c, 1.25)              # Kristallbruch-Linien (diagonal)
                if n > 0.985: c = (255, 255, 255)
            elif k == 'snow':
                c = mix((250, 252, 255), (196, 214, 236), t * 0.6 + n * 0.15); c = sh(c, lightf)
            elif k == 'rust':
                c = mix(sh(base, lightf * grad), sh(m.accent, lightf), (hsh(seed, x, 3) * 0.6 + t * 0.4) if side else n * 0.5)
                if side and hsh(seed, x, 11) > 0.8 and y > fh * hsh(seed, x, 12): c = sh(m.accent, 0.9)   # Rostfahnen
                if edge: c = sh(c, 0.7)
                if n > 0.97: c = sh(c, 0.6)
            elif k == 'rock':
                v = 0.85 + 0.25 * hsh(seed, x // 2, y // 2) + 0.08 * n
                c = sh(c, v)
                if hsh(seed, x + y // 2, 5) > 0.92 or hsh(seed, x - y // 3, 9) > 0.95: c = sh(c, 0.6)   # Risse
                if edge: c = sh(c, 0.82)
            elif k == 'obsidian':
                c = sh(base, lightf * (0.9 + 0.2 * n))
                if (x + y) % 9 == 0 and n > 0.4: c = mix(c, m.accent, 0.5)        # Glanz
                if edge: c = sh(c, 0.7)
            elif k == 'runes':
                c = sh(base, lightf * (0.9 + 0.15 * n))
                gx, gy = x % 6, y % 8
                rune = side and fw >= 4 and fh >= 6 and 1 <= gx <= 4 and 1 <= gy <= 6 and hsh(seed, x // 6, y // 8, gx, gy) > 0.62 and (gx in (1, 4) or gy in (1, 3, 6))
                if rune: c = m.glow_col; g = m.glow_col
                if edge: c = sh(c, 0.7) if not rune else c
            elif k == 'graphite':
                gx, gy = x % 5, y % 5
                c = sh(base, lightf * (0.95 + 0.1 * n))
                if gx == 0 or gy == 0: c = sh(c, 0.6)
                elif gx == 1 or gy == 1: c = sh(c, 1.15)
            elif k == 'crust':   # Lava-Kruste: dunkel mit gluehenden Rissen
                v = hsh(seed, x // 3, y // 3)
                c = sh(base, lightf * (0.8 + 0.3 * n))
                crack = (abs(((x * 0.7 + y * 1.3 + v * 9) % 7) - 3.5) < 0.6) or (abs(((x * 1.4 - y * 0.6 + v * 5) % 9) - 4.5) < 0.5)
                if crack: c = mix(m.glow_col, (255, 240, 160), 0.3 * n); g = c
                elif edge: c = sh(c, 0.7)
            elif k == 'scale':
                row = y // 3
                xo = (x + (row % 2) * 2) % 4
                c = sh(base, lightf * grad * (0.95 + 0.08 * n))
                if y % 3 == 2: c = sh(c, 0.72)
                elif xo == 0: c = sh(c, 0.82)
                elif y % 3 == 0 and xo in (1, 2): c = mix(c, m.accent, 0.45)
            elif k == 'flesh':
                v = hsh(seed, x // 2, y // 2)
                c = mix(sh(base, lightf * grad), sh(m.accent, lightf), v * 0.5)
                if abs(math.sin(x * 0.9 + y * 0.35 + v * 3)) < 0.12: c = sh(m.accent, 0.8)       # Adern
            elif k == 'crystal':
                band = ((x + y * 2) // 3) % 3
                c = sh(mix(base, m.accent, band / 2.0), lightf)
                if edge: c = sh(c, 1.25)
                core = 0 < x < fw - 1 and 0 < y < fh - 1 and hsh(seed, x, y, 4) > 0.8
                if core: c = sh(m.accent, 1.2); g = c
            elif k == 'pvc':
                c = sh(base, lightf * (1.04 - 0.14 * t))
                if fw >= 6 and x % 6 == 0 or y == 0: c = sh(c, 0.86)
                if x == 1 and side: c = sh(c, 1.08)
                if n > 0.99: c = sh(c, 0.8)
            elif k == 'pipe':
                c = sh(base, lightf * (0.85 + 0.25 * math.sin((x / max(1, fw - 1)) * math.pi)))
                if y % 6 == 0: c = sh(m.accent, lightf)
            elif k == 'cap':      # Pilzhut mit Tupfen
                c = sh(base, lightf * grad * (0.92 + 0.12 * n))
                if hsh(seed, x // 3, y // 3) > 0.72 and (x % 3) and (y % 3): c = sh(m.accent, lightf)
            elif k == 'gill':
                c = sh(base if x % 2 else m.accent, lightf)
                if hsh(seed, x, 2) > 0.8: g = sh(m.glow_col or base, 1.0); c = g
            elif k == 'fiber':
                c = sh(base, lightf * (0.85 + 0.25 * hsh(seed, x, 1)) * (0.95 + 0.1 * n))
                if x % 3 == 0: c = sh(c, 0.8)
            elif k == 'bone':
                c = sh(base, lightf * grad * (0.92 + 0.12 * n))
                if edge: c = sh(c, 0.8)
            elif k == 'cloth':
                c = sh(base, lightf * grad * (0.9 + 0.1 * ((x + y) % 2)))
                if y == fh - 1: c = sh(m.accent, lightf)
            elif k == 'glow':
                d = min(x, y, fw - 1 - x, fh - 1 - y) / max(1.0, min(fw, fh) / 2.0)
                c = mix(m.base, m.accent, min(1.0, d * 1.2)); g = c
            elif k == 'eye':
                cx, cy = (fw - 1) / 2, (fh - 1) / 2
                d = math.hypot(x - cx, y - cy) / max(1.0, max(fw, fh) / 2)
                c = mix(m.accent, m.base, min(1.0, d)); g = c
                if x == int(cx) and y == max(0, int(cy) - 1): c = (255, 255, 255); g = c
            elif k == 'plain':
                c = sh(base, lightf * grad)
                if edge: c = sh(c, 0.85)
            put(x, y, c, g)

def paint(roots, uv, size, cubes_faces):
    tex = Image.new('RGBA', (size, size), (0, 0, 0, 0)); glow = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    for r in roots:
        for b, _ in r.walk():
            for i, c in enumerate(b.cubes):
                u, v = uv[(id(b), i)]
                m = M[c.mat]
                for face, rect in cubes_faces(u, v, c).items():
                    paint_face(tex, glow, rect, face, m, zlib.crc32((b.name + str(i) + face).encode()))
    return tex, glow
