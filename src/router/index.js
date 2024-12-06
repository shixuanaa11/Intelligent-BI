import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
// import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
