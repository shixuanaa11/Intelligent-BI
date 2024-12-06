import router from '@/router'
import NProgress from 'nprogress' // progress bar
import { useUserStore } from '@/stores/user'
import ACCESS_ENUM from '@/access/accessEnum'
import checkAccess from './checkAccess'
import 'nprogress/nprogress.css'
import { message } from 'ant-design-vue'
/**
 * Author: axuan
 */
// 全局路由权限
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  console.log('路由拦截')
  //  获取当前登录用户
  const loginStore = useUserStore()
  let loginUser = loginStore.userInfo
  //  如果之前没获取过登录信息就自动登录
  if (!loginUser || !loginUser.userRole) {
    await loginStore.FETCH_USERINFO()
    // 这里让pinia获取到登录信息后，重新获取一次
    loginUser = loginStore.userInfo
  }

  //  当前页面需要的权限,如果连meta都没有就是没有登录就能访问的页面
  const needAccess = to.meta?.access ?? ACCESS_ENUM.NOT_LOGIN
  //  受跳转的页面必须登录才能访问，判断是否是要登录才能访问的页面
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    //  如果没登录就跳转到登录页面+页面重定向,这里3重判断
    //  登录用户不存在 (未登录)
    //  登录用户存在，但是没有角色信息 (未登录)
    //  登录用户存在，但是角色信息为未登录 (未登录) 这个在一开始判断
    if (!loginUser || !loginUser.userRole || loginUser.userRole == ACCESS_ENUM.NOT_LOGIN) {
      message.error('未登录，请先登录')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
    //  如果已经登录，判断权限是否足够，如果不足，跳到无权限页面（403)
    //  这里调用之前写的函数，判断是否有权限
    if (!checkAccess(loginUser, needAccess)) {
      next(`/noAuth`)
      return
    }
  }
  next()
})
router.afterEach(() => {
  NProgress.done()
})
