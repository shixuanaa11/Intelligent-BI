import { ref } from 'vue'
import { defineStore } from 'pinia'

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
    SET_USERINFO,
    SET_TABLEDATA,
    GET_SINGLE_TABLEDATA,
  }
})
