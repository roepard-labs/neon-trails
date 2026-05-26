import './style.css'
import 'vue-sonner/style.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { MotionPlugin } from '@vueuse/motion'
import { autoAnimatePlugin } from '@formkit/auto-animate/vue'

import App from './App.vue'
import router from './router'
import { usePresentationStore } from '@/stores/presentation'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(MotionPlugin)
app.use(autoAnimatePlugin)

// Mantiene el store de presentación al día con la ruta activa (sección actual
// + marca de "visitada") para el breadcrumb y la barra de progreso.
router.afterEach((to) => {
  if (typeof to.name === 'string') {
    usePresentationStore().setCurrent(to.name)
  }
})

app.mount('#app')
