import axios from 'axios'
import.meta.env
import { message } from 'ant-design-vue'

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
      message.error('未登录')
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
