import axios from 'axios'
import.meta.env
import { message } from 'ant-design-vue'
// import { useRouter } from 'vue-router'
// const router = useRouter()
// 免登录名单
// const allowList = ['login', 'register']

const instance = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 60000,
})

// 请求携带cookie
instance.defaults.withCredentials = true

instance.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    if (response.data.code === 40100) {
      // 如果请求报了未登录
      // 这个请求不是获取用户信息的请求，并且不是在登录 注册页面，则跳转到登录页面
      // if (
      //   !response.request.responseURL.includes('user/get/login') &&
      //   !window.location.pathname.includes(allowList)
      // ) {
      //   // router.push({ path: '/user/login', query: { redirect: window.location.href } })
      //   window.location.href = `/user/login?redirect=${window.location.href}`
      // message.error('未登录')
      //   return response.data
      // }
      // message.error(response.data.message)
      return response.data
    }
    return response.data
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    message.error('请求失败')
    return Promise.reject(error)
  },
)
export default instance
