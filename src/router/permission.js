import router from '@/router/index'
// import { useUserStore } from '@/stores/user'
import NProgress from 'nprogress' // progress bar

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (to.matched.length === 0) {
    from.path ? next({ name: from.name }) : next('/404')
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})
