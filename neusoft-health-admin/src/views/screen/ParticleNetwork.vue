<template>
  <div ref="containerRef" class="particle-network"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as THREE from 'three'

const containerRef = ref<HTMLElement>()

let renderer: THREE.WebGLRenderer | null = null
let scene: THREE.Scene | null = null
let camera: THREE.OrthographicCamera | null = null
let particlesMesh: THREE.Points | null = null
let linesMesh: THREE.LineSegments | null = null
let positions = new Float32Array(0)
let velocities = new Float32Array(0)
let linePositions = new Float32Array(0)
let lineColors = new Float32Array(0)
let count = 0
let mouseX = -9999
let mouseY = -9999
let animId = 0
let w = 0
let h = 0

const CONN_DIST = 150
const SPEED = 0.35
const P_SIZE = 1.8
const P_COLOR = '#00d4ff'
const L_COLOR = '#00d4ff'
const MOUSE_R = 180
const DENSITY = 0.00008

function cleanup() {
  if (animId) { cancelAnimationFrame(animId); animId = 0 }
  if (particlesMesh) {
    particlesMesh.geometry.dispose()
    ;(particlesMesh.material as THREE.Material).dispose()
    particlesMesh = null
  }
  if (linesMesh) {
    linesMesh.geometry.dispose()
    ;(linesMesh.material as THREE.Material).dispose()
    linesMesh = null
  }
  if (renderer) {
    const cvs = renderer.domElement
    if (cvs.parentElement) cvs.parentElement.removeChild(cvs)
    renderer.dispose()
    renderer = null
  }
  scene = null
  camera = null
}

function build() {
  const el = containerRef.value
  if (!el) return
  w = el.clientWidth || window.innerWidth
  h = el.clientHeight || window.innerHeight
  if (w < 10 || h < 10) return

  count = Math.max(30, Math.min(180, Math.floor(w * h * DENSITY)))

  scene = new THREE.Scene()
  camera = new THREE.OrthographicCamera(0, w, h, 0, -1, 1)

  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
  renderer.setSize(w, h)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.setClearColor(0x000000, 0)
  el.appendChild(renderer.domElement)

  positions = new Float32Array(count * 3)
  velocities = new Float32Array(count * 2)
  for (let i = 0; i < count; i++) {
    positions[i * 3] = Math.random() * w
    positions[i * 3 + 1] = Math.random() * h
    const a = Math.random() * Math.PI * 2
    const s = (0.2 + Math.random() * 0.8) * SPEED
    velocities[i * 2] = Math.cos(a) * s
    velocities[i * 2 + 1] = Math.sin(a) * s
  }

  const pg = new THREE.BufferGeometry()
  pg.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  particlesMesh = new THREE.Points(pg, new THREE.PointsMaterial({
    size: P_SIZE, color: new THREE.Color(P_COLOR),
    transparent: true, opacity: 0.7,
    blending: THREE.AdditiveBlending, depthWrite: false, sizeAttenuation: false
  }))
  scene.add(particlesMesh)

  const maxL = count * count
  linePositions = new Float32Array(maxL * 6)
  lineColors = new Float32Array(maxL * 6)
  const lg = new THREE.BufferGeometry()
  lg.setAttribute('position', new THREE.BufferAttribute(linePositions, 3))
  lg.setAttribute('color', new THREE.BufferAttribute(lineColors, 3))
  linesMesh = new THREE.LineSegments(lg, new THREE.LineBasicMaterial({
    vertexColors: true, transparent: true, opacity: 0.5,
    blending: THREE.AdditiveBlending, depthWrite: false
  }))
  scene.add(linesMesh)
}

function tick() {
  animId = requestAnimationFrame(tick)
  if (!renderer || !scene || !camera) return

  for (let i = 0; i < count; i++) {
    const ix = i * 3
    let x = positions[ix] + velocities[i * 2]
    let y = positions[ix + 1] + velocities[i * 2 + 1]
    if (x < 0 || x > w) { velocities[i * 2] *= -1; x = Math.max(0, Math.min(w, x)) }
    if (y < 0 || y > h) { velocities[i * 2 + 1] *= -1; y = Math.max(0, Math.min(h, y)) }

    const dx = mouseX - x, dy = mouseY - y
    const d = Math.sqrt(dx * dx + dy * dy)
    if (d < MOUSE_R && d > 0) {
      const f = (1 - d / MOUSE_R) * 0.03
      velocities[i * 2] += dx / d * f
      velocities[i * 2 + 1] += dy / d * f
    }
    if (velocities[i * 2] ** 2 + velocities[i * 2 + 1] ** 2 > 2.25) {
      velocities[i * 2] *= 0.95; velocities[i * 2 + 1] *= 0.95
    }
    positions[ix] = x; positions[ix + 1] = y
  }
  particlesMesh!.geometry.attributes.position.needsUpdate = true

  let li = 0
  const bc = new THREE.Color(L_COLOR)
  for (let i = 0; i < count; i++) {
    for (let j = i + 1; j < count; j++) {
      const dx = positions[i * 3] - positions[j * 3]
      const dy = positions[i * 3 + 1] - positions[j * 3 + 1]
      const d = Math.sqrt(dx * dx + dy * dy)
      if (d < CONN_DIST) {
        const a = 1 - d / CONN_DIST
        const o = li * 6
        linePositions[o] = positions[i * 3]; linePositions[o + 1] = positions[i * 3 + 1]
        linePositions[o + 3] = positions[j * 3]; linePositions[o + 4] = positions[j * 3 + 1]
        lineColors[o] = bc.r * a; lineColors[o + 1] = bc.g * a; lineColors[o + 2] = bc.b * a
        lineColors[o + 3] = bc.r * a; lineColors[o + 4] = bc.g * a; lineColors[o + 5] = bc.b * a
        li++
      }
    }
  }
  for (let i = li * 6; i < linePositions.length; i++) { linePositions[i] = 0; lineColors[i] = 0 }
  linesMesh!.geometry.attributes.position.needsUpdate = true
  linesMesh!.geometry.attributes.color.needsUpdate = true

  renderer.render(scene, camera)
}

function onMove(e: MouseEvent) {
  const el = containerRef.value
  if (!el) return
  const r = el.getBoundingClientRect()
  mouseX = e.clientX - r.left; mouseY = e.clientY - r.top
}
function onLeave() { mouseX = -9999; mouseY = -9999 }

function rebuild() {
  cleanup()
  setTimeout(() => { build(); tick() }, 50)
}

let resizeTimer = 0
function debouncedRebuild() {
  clearTimeout(resizeTimer)
  resizeTimer = window.setTimeout(rebuild, 200)
}

onMounted(() => {
  build()
  tick()
  window.addEventListener('mousemove', onMove)
  window.addEventListener('mouseleave', onLeave)
  document.addEventListener('fullscreenchange', rebuild)
  window.addEventListener('resize', debouncedRebuild)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onMove)
  window.removeEventListener('mouseleave', onLeave)
  document.removeEventListener('fullscreenchange', rebuild)
  window.removeEventListener('resize', debouncedRebuild)
  clearTimeout(resizeTimer)
  cleanup()
})
</script>

<style scoped>
.particle-network {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}
</style>
