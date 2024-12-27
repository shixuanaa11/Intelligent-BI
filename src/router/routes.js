import ACCESS_ENUM from '@/access/accessEnum'

export const routes = [
  {
    path: '/',
    redirect: '/home',
    component: () => import('@/views/Layout/BasicLayout.vue'),
    children: [
      {
        path: '/home',
        name: 'home',
        component: () => import('@/views/Pages/homePage.vue'),
      },
      {
        path: '/chart',
        name: 'chart',
        component: () => import('@/views/Pages/ChartPage.vue'),
      },
      {
        path: '/chart/Async',
        name: 'chartAsync',
        component: () => import('@/views/Pages/ChartAsyncPage.vue'),
      },
      {
        path: '/my',
        name: 'my',
        component: () => import('@/views/Pages/My.vue'),
        meta: {
          access: ACCESS_ENUM.USER,
        },
      },
      {
        path: '/mychart',
        name: 'mychart',
        component: () => import('@/views/Pages/MyChart.vue'),
        meta: {
          access: ACCESS_ENUM.USER,
        },
      },
      {
        path: '/add_picture',
        name: 'add_picture',
        component: () => import('@/views/Pages/picture/AddPicturePage.vue'),
      },
      // 指定id的图片详情页，通过props页面间传递id
      {
        path: '/picture/:id',
        name: 'picture',
        component: () => import('@/views/Pages/picture/PictureDetailPage.vue'),
        props: true,
        meta: {
          access: ACCESS_ENUM.USER,
        },
      },
      {
        path: '/admin/userManager',
        name: 'adminUserManager',
        component: () => import('@/views/Pages/admin/UserManagerPage.vue'),
        meta: {
          access: ACCESS_ENUM.ADMIN,
        },
      },
      {
        path: '/admin/pictureManager',
        name: 'adminPictureManager',
        component: () => import('@/views/Pages/admin/PictureManagerPage.vue'),
        meta: {
          access: ACCESS_ENUM.ADMIN,
        },
      },
      {
        path: '/add_picture/batch',
        name: 'add_picture_Batch',
        component: () => import('@/views/Pages/picture/AddPictureBetchPage.vue'),
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
  // 403页面（1级）
  { path: '/noAuth', component: () => import('@/components/noAuth.vue'), hidden: true },

  //这个*匹配必须放在最后，将改路由配置放到所有路由的配置信息的最后，否则会其他路由path匹配造成影响。
  {
    path: '/:catchAll(.*)', // 不识别的path自动匹配404
    redirect: '/404',
  },
]
