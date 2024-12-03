<template>
  <a-layout>
    <a-layout-header :style="{ width: '100%', background: '#fff' }">
      <a-row>
        <a-col flex="150px">
          <div class="logo">
            <img src="@/assets/logo.png" class="img" alt="" />
            <div class="title">智能BI</div>
          </div></a-col
        >
        <a-col flex="auto"
          ><a-menu
            v-model:selectedKeys="selectedKeys"
            mode="horizontal"
            :style="{ lineHeight: '64px' }"
          >
            <a-menu-item key="/" @click="changeMenu('home')">
              <span>主页</span>
            </a-menu-item>
            <a-menu-item key="/chart" @click="changeMenu('chart')">
              <span>图表</span>
            </a-menu-item>
            <a-menu-item key="/mychart" @click="changeMenu('mychart')">
              <span>图表管理</span>
            </a-menu-item>
            <a-menu-item key="/my" @click="changeMenu('my')">
              <span>我的</span>
            </a-menu-item>
          </a-menu></a-col
        >
        <a-col flex="90px">
          <a-button type="primary" v-if="!userStore.userInfo?.id" @click="tologin">登录</a-button>
          <a-dropdown :arrow="{ pointAtCenter: true }" placement="bottom" v-else>
            <a class="ant-dropdown-link" @click.prevent>
              <a-avatar :src="avatar()" style="margin-right: 8px" />
              <span>
                {{
                  userStore.userInfo.username
                    ? userStore.userInfo.username
                    : userStore.userInfo.account
                }}
              </span>
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item>
                  <a href="javascript:;">1st menu item</a>
                </a-menu-item>
                <a-menu-item>
                  <a href="javascript:;">2nd menu item</a>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item @click="logout">
                  <a href="javascript:;">退出登录</a>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-col>
      </a-row>
    </a-layout-header>
    <a-layout-content :style="{ padding: '0 50px' }">
      <a-breadcrumb :style="{ margin: '16px 0' }">
        <a-breadcrumb-item>Home</a-breadcrumb-item>
        <a-breadcrumb-item>List</a-breadcrumb-item>
        <a-breadcrumb-item>App</a-breadcrumb-item>
      </a-breadcrumb>
      <div :style="{ background: '#fff', padding: '24px', minHeight: '530px' }">
        <router-view></router-view>
      </div>
    </a-layout-content>
    <a-layout-footer :style="{ textAlign: 'center' }">
      Intelligent BI ©2024 Created by axuan
    </a-layout-footer>
  </a-layout>
</template>
<script setup>
import { ref } from 'vue'
const selectedKeys = ref(['/'])
// 路由
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()
// 接口
import { UserlLogout } from '@/api/user'
// pinia
import { useUserStore } from '@/stores/user'
import { message } from 'ant-design-vue'
const userStore = useUserStore()
// 切换菜单
const changeMenu = (path) => {
  console.log(path)
  console.log(route.path)
  router.push({ name: path })
}
// 跳转登录页
const tologin = () => {
  router.push({ name: 'login' })
}
// 注销 退出登录
const logout = async () => {
  const res = await UserlLogout()
  if (res.code === 0) {
    message.success('注销成功')
    // 清空用户信息
    userStore.SET_USERINFO({})
    router.push({ name: 'login' })
  }
}
// 头像
const avatar = () => {
  return userStore.userInfo.avatarUrl
    ? userStore.userInfo.avatarUrl
    : 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png'
}
</script>
<style scoped>
.site-layout .site-layout-background {
  background: #fff;
}

[data-theme='dark'] .site-layout .site-layout-background {
  background: #141414;
}
.logo {
  display: flex;
  align-items: center;
  text-align: center;
  width: 100px;
  /* justify-content: center; */
  .img {
    height: 36px;
  }
  .title {
    margin-left: 10px;
  }
}
</style>
