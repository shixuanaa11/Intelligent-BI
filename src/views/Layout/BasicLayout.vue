<template>
  <a-layout>
    <a-layout-header :style="{ width: '100%', background: '#fff' }">
      <a-row :wrap="false">
        <a-col flex="150px">
          <div class="logo">
            <img src="@/assets/logo.png" class="img" alt="" />
            <div class="title">智能BI</div>
          </div></a-col
        >
        <a-col flex="auto"
          ><a-menu :selectedKeys="[route.path]" mode="horizontal" :style="{ lineHeight: '64px' }">
            <a-menu-item key="/home" @click="changeMenu('home')">
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
                  <a href="javascript:;">个人主页</a>
                </a-menu-item>
                <!-- <a-menu-item>
                  <a href="javascript:;">2nd menu item</a>
                </a-menu-item> -->
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
import { onMounted, ref } from 'vue'
const selectedKeys = ref(['/home'])
// 路由
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()
// 路由变量
// import { routes } from '@/router/routes'
// const menuRouter = ref([routes[0].children])
// 菜单切换
// const aa = ({ key }) => {
//   console.log('菜单切换', key)
// }
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
// 路由更新时自动更新选中的菜单项
router.afterEach((to) => {
  console.log('菜单更新', to)

  selectedKeys.value = [to.path]
})
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
