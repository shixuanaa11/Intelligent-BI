import request from '@/utils/myaxios'

// 登录接口
export const UserLogin = ({ account, password }) =>
  request.post('/user/login', { account, password })

// 注册接口
export const UserRegister = (data) => request.post('/user/register', data)
// 注销接口
export const UserlLogout = () => request.post('/user/logout')
