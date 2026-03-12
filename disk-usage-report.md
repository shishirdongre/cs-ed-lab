# Disk Usage Report — Next Steps

**Generated:** 2025-03-12 (updated)  
**Disk:** 938G total, **750G used (85%)**, 140G free  
**Filesystem:** `/dev/nvme0n1p2` (root)

**Change since last report:** 865G → 750G used (**~115G freed**)

---

## Cleanup Completed (2025-03-12)

| Item | Est. Freed |
|------|------------|
| SJSU Adv Topics assignment_2 venv | **5.4G** |
| BB-MAS_Dataset | **38G** |
| 1brc measurement datasets | **~34G** |
| Steam cache | **~650M** |
| Stale node_modules (37+ projects) | **~5G** |
| Stale Python venvs (20 dirs) | **~8G** |
| **Total freed** | **~115G** |

---

## Top-Level Home Directory Usage

| Size | Path |
|------|------|
| **103G** | `/home/shishirdongre/Downloads/` |
| **58G** | `/home/shishirdongre/Videos/` |
| **55G** | `/home/shishirdongre/VirtualBox VMs/` |
| **22G** | `/home/shishirdongre/rust/` |
| **16G** | `/home/shishirdongre/SJSU/` |
| **11G** | `/home/shishirdongre/Music/` |
| **3.8G** | `/home/shishirdongre/Documents/` |
| **2.6G** | `/home/shishirdongre/csed_lab/` |
| **1.7G** | `/home/shishirdongre/jams/` |
| **1.5G** | `/home/shishirdongre/go/` |
| **1.2G** | `/home/shishirdongre/Pictures/` |
| 899M | `Applications/` |
| 811M | `learn/` |
| 775M | `GRE/` |
| 696M | `python/` |

---

## Hidden Dirs (Cache, Tools, Envs)

| Size | Path |
|------|------|
| **102G** | `~/.local/share/` — **Steam 93G**, akonadi 5.2G, cursor-agent 2.7G |
| **39G** | `~/.cache/` |
| **5.6G** | `~/.npm/` |
| **2.9G** | `~/.rustup/` |
| **2.8G** | `~/.pyenv/` |
| **1.1G** | `~/.cursor/` |
| **693M** | `~/.cargo/` |

### ~/.cache breakdown

| Size | Subdir |
|------|--------|
| 12G | `yay/` (AUR package cache) |
| 9.9G | `pip/` |
| 3.6G | `vscode-cpptools/` |
| 3.2G | `huggingface/` |
| 2.1G | `spotify/` |
| 2.0G | `mozilla/` |
| 2.0G | `chromium/` |
| 1.6G | `ms-playwright/` |
| 1.3G | `yarn/` |

### ~/.local/share breakdown

| Size | Subdir |
|------|--------|
| **93G** | `Steam/` |
| 5.2G | `akonadi/` |
| 2.7G | `cursor-agent/` |
| 736M | `Trash/` |

---

## SJSU Subdirs (after cleanup)

| Size | Path |
|------|------|
| **9.0G** | `SJSU/APP/` |
| **2.8G** | `SJSU/Adv Topics in CS/` |
| 1.4G | `SJSU/Archive/` |
| 1022M | `SJSU/NoSQL/` |
| 770M | `SJSU/Topics_in_Cloud/` |
| 205M | `SJSU/APLP/` |
| 34M | `SJSU/sjhacks/` |

---

## Remaining Python venvs

| Size | Path |
|------|------|
| **904M** | `~/csed_lab/cs-ed-lab/java-ml-project/venv` |

---

## Remaining node_modules (top)

| Size | Path |
|------|------|
| 61M | `~/.cache/typescript/5.5/node_modules` |
| 29M | `~/.npm/_npx/.../node_modules` |
| 4.5M | `~/csed_lab/cs-ed-lab/sentiment-api/node_modules` |
| (rest in .cache, .npm, .vscode) | |

---

## System Dirs

| Size | Path |
|------|------|
| **23G** | `/var/cache/` |
| 2.4G | `/var/log/` |
| 24G | `/usr/` |

### /var/cache

| Size | Path |
|------|------|
| **22G** | `pacman/` (Manjaro package cache) |

---

## Quick Wins (Low-Risk Cleanup)

| Action | Est. Space | Command / Notes |
|--------|------------|------------------|
| Clear pip cache | ~10G | `pip cache purge` |
| Clear yay cache | ~12G | `yay -Scc` (or `yay -Sc`) |
| Clear pacman cache | ~22G | `sudo pacman -Scc` |
| Clear huggingface cache | ~3.2G | `rm -rf ~/.cache/huggingface/*` |
| Clear vscode-cpptools cache | ~3.6G | `rm -rf ~/.cache/vscode-cpptools/*` |
| Empty Trash | ~736M | `rm -rf ~/.local/share/Trash/*` |
| Clear yarn cache | ~1.3G | `yarn cache clean` |
| Remove java-ml-project venv | ~904M | `rm -rf ~/csed_lab/cs-ed-lab/java-ml-project/venv` (close Cursor first) |

---

## Bigger Decisions

| Item | Size | Notes |
|------|------|-------|
| **Steam games** | **93G** | `~/.local/share/Steam/` — uninstall unused games |
| **Downloads** | **103G** | Archive or delete old downloads |
| **Videos** | **58G** | Move to external or delete |
| **VirtualBox VMs** | **55G** | Remove unused VMs |
| **akonadi** | **5.2G** | KDE PIM/cache — consider if unused |

---

## Useful Commands

```bash
# Full disk usage by dir (slow on large trees)
du -h --max-depth=1 /path/to/dir | sort -hr | head -20

# Find all node_modules and their sizes
find ~ -type d -name "node_modules" -prune -exec du -sh {} \; 2>/dev/null | sort -hr

# Find all venv/.venv and their sizes
find ~ -type d \( -name "venv" -o -name ".venv" \) -not -path "*site-packages*" -exec du -sh {} \; 2>/dev/null | sort -hr

# Interactive disk usage (if ncdu installed)
ncdu ~
```

---

## Summary

- **Disk:** 85% used (750G), 140G free — improved from 98% (865G).
- **Largest:** Steam (93G), Downloads (103G), Videos (58G), VirtualBox (55G).
- **Caches:** ~50G in pip, yay, pacman, huggingface, vscode-cpptools, etc.
- **SJSU:** Down from 107G to 16G after cleanup.
- **Remaining venv:** java-ml-project (904M) — locked by Cursor, remove manually if needed.

Start with cache cleanup (pip, yay, pacman) for ~44G with minimal risk.

---

## Large Files in Downloads (>50M)

| Size | Path |
|------|------|
| **22G** | `Archive/WinDev2308Eval.VirtualBox.zip` |
| **22G** | `Archive/WinDev2308Eval.ova` |
| **8.0G** | `Archive/train-001.csv` |
| **5.8G** | `Win10_22H2_English_x64v1.iso` |
| **5.8G** | `Archive/ubuntu-24.04.1-desktop-amd64.iso` |
| **5.8G** | `Archive/ubuntu-24.04.1-desktop-amd64(1).iso` |
| **2.0G** | `Las Vegas - 25-20250521T010918Z-1-005.zip` (+ 4 more ~2G each) |
| **1.5G** | `BB-MAS_Dataset.zip` |
| **1.5G** | `Archive/Rangitaranga (2015)...mkv` |
| 627M | `Las Vegas - 25-20250521T010918Z-1-006.zip` |
| 610M | `Archive/GRE-20180724T173945Z-001.zip` |
| 524M | `spark-4.0.1-bin-hadoop3.tgz` |
| ... | (see previous report for full list) |

**Top candidates for deletion:**
- WinDev2308Eval.ova + .zip (44G combined — keep one if needed)
- train-001.csv (8G)
- Ubuntu ISOs — duplicate (5.8G × 2)
- Las Vegas - 25 zips (~13G)
- BB-MAS_Dataset.zip (1.5G — dataset already deleted)
