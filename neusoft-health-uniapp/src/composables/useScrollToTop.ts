import { ref, nextTick } from 'vue'
import { onShow } from '@dcloudio/uni-app'

let idCounter = 0

export function useScrollToTop() {
  const scrollTop = ref(0)

  onShow(() => {
    scrollTop.value = ++idCounter
    nextTick(() => {
      scrollTop.value = 0
    })
  })

  return { scrollTop }
}
