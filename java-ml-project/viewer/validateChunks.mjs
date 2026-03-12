#!/usr/bin/env node
/**
 * Validates chunk line ranges against actual source files.
 * Run: node validateChunks.mjs
 */
import { readFileSync } from 'fs'
import { join, dirname } from 'path'
import { fileURLToPath } from 'url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const sourcesDir = join(__dirname, 'src', 'sources')
const chunksPath = join(__dirname, 'src', 'chunks.json')

const chunksData = JSON.parse(readFileSync(chunksPath, 'utf8'))
const chunks = chunksData.filter((c) => c.file && c.startLine && c.endLine)

let allValid = true
for (const c of chunks) {
  const path = join(sourcesDir, `${c.file}.java`)
  let content
  try {
    content = readFileSync(path, 'utf8')
  } catch (e) {
    allValid = false
    console.error(`❌ Chunk ${c.id}: file ${c.file}.java not found`)
    continue
  }
  const lines = content.split('\n')
  const totalLines = lines.length

  const valid = c.startLine >= 1 && c.endLine <= totalLines && c.startLine <= c.endLine
  if (!valid) {
    allValid = false
    console.error(`❌ Chunk ${c.id}: ${c.file} ${c.startLine}-${c.endLine} INVALID (file has ${totalLines} lines)`)
  } else {
    console.log(`✓ Chunk ${c.id}: ${c.file} ${c.startLine}-${c.endLine} OK`)
  }
}

if (allValid) {
  console.log('\n✅ All chunks validated successfully.')
} else {
  process.exit(1)
}
