import { ref } from 'vue'
import { defineStore } from 'pinia'
import { GetLoginUserInfo } from '@/api/user'
import ACCESS_ENUM from '@/access/accessEnum'
export const useUserStore = defineStore('user', () => {
  const Token = ref('')
  const userInfo = ref({})
  const tableData = ref([])
  const SET_TOKEN = (token) => {
    Token.value = token
  }

  const SET_USERINFO = (user) => {
    userInfo.value = user
  }
  const FETCH_USERINFO = async () => {
    const res = await GetLoginUserInfo()
    console.log('获取用户信息')
    // 如果登录成功，将用户信息存储到本地
    if (res.code == 0 && res.data.data) {
      SET_USERINFO(res.data)
    } else {
      // 如果登录失败，也将用户信息设置为未登录，表示你已经尝试执行过登录了
      // 通过这个状态我们就能知道有没有尝试过获取登录信息，防止重复的获取
      // 提示这个方法在路由拦截用
      userInfo.value = { userRole: ACCESS_ENUM.NOT_LOGIN }
    }
  }
  const SET_TABLEDATA = (data) => {
    tableData.value = data
  }

  // 通过id获取单条数据
  const GET_SINGLE_TABLEDATA = (id) => {
    const index = tableData.value.findIndex((item) => item.id === id)
    console.log('index', index)
    console.log('tableData.value[index]', tableData.value[index])
    return tableData.value[index]
    // return tableData.value[index]
  }
  // 通过id修改单条数据(给接口做)
  return {
    Token,
    userInfo,
    tableData,
    SET_TOKEN,
    FETCH_USERINFO,
    SET_USERINFO,
    SET_TABLEDATA,
    GET_SINGLE_TABLEDATA,
  }
})
