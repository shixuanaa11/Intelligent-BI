import ACCESS_ENUM from './accessEnum'
/**
 * Author: axuan
 * 判断页面权限的方法 函数
 * @param {*} loginUser 登录用户
 * @param {*} needAccess 判断权限的页面
 */
const checkAccess = (loginUser, needAccess) => {
  // 看登录用户存不存在，不存在自然就是未登录了，给他赋值一个未登录的权限NOT_LOGIN
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN
  // 不用登录就能访问的页面
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true
  }
  // 登录才能访问的页面
  if (needAccess === ACCESS_ENUM.USER) {
    // 判断一下用户是否登录,如果未登录表示无权限false走掉
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false
    }
  }
  // 管理员才能访问的页面
  if (needAccess === ACCESS_ENUM.ADMIN) {
    // 判断一下用户是否是管理员,如果不是管理员表示无权限false走掉
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false
    }
  }
  return true
}
export default checkAccess
