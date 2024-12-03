import { createRouter, createWebHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home',
      component: () => import('@/views/Layout/BasicLayout.vue'),
      children: [
        {
          path: '/home',
          name: 'home',
          component: () => import('@/views/Pages/home.vue'),
        },
        {
          path: '/chart',
          name: 'chart',
          component: () => import('@/views/Pages/ChartPage.vue'),
        },
        {
          path: '/my',
          name: 'my',
          component: () => import('@/views/Pages/My.vue'),
        },
        {
          path: '/mychart',
          name: 'mychart',
          component: () => import('@/views/Pages/MyChart.vue'),
        },
      ],
    },

    // 登录注册（1级）
    {
      path: '/user',
      component: () => import('@/views/Layout/LoginLayout.vue'),

      children: [
        {
          path: 'login',
          name: 'login',
          component: () => import('@/views/Pages/login/Login.vue'),
        },
        {
          path: 'register',
          name: 'register',
          component: () => import('@/views/Pages/login/Register.vue'),
        },
      ],
    },
    // 404页面（1级）
    { path: '/404', component: () => import('@/components/error.vue'), hidden: true },

    //这个*匹配必须放在最后，将改路由配置放到所有路由的配置信息的最后，否则会其他路由path匹配造成影响。
    {
      path: '/:catchAll(.*)', // 不识别的path自动匹配404
      redirect: '/404',
    },
  ],
})

export default router
