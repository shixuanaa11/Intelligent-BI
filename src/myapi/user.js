import request from '@/utils/myaxios'

// 登录接口
export const UserLogin = ({ account, password }) =>
  request.post('/user/login', { account, password })

// 注册接口
export const UserRegister = (data) => request.post('/user/register', data)
// 注销接口
export const UserlLogout = () => request.post('/user/logout')
// 获取用户信息接口
export const GetLoginUserInfo = () => request.get('/user/get/login')
// 获取用户列表（管理员）
export const listUserVOByPage = (data) => request.post('/user/list/page/vo', data)
// 删除用户（管理员）
export const deleteUser = (id) => request.post('user/delete', id)
