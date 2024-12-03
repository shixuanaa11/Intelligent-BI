<template>
  <div class="main">
    <a-form
      id="formLogin"
      class="user-layout-login"
      ref="formLogin"
      @finish="handleSubmit()"
      :model="Logindata"
      :rules="LoginRules"
      :form="form"
    >
      <a-tabs v-model:activeKey="activeKey" centered>
        <a-tab-pane key="tab1" tab="账号密码登录">
          <a-alert
            v-if="isLoginError"
            type="error"
            showIcon
            style="margin-bottom: 24px"
            message="账号或密码错误"
          />
          <a-form-item name="account">
            <a-input
              size="large"
              type="text"
              placeholder="请输入账号"
              v-model:value="Logindata.account"
            >
              <template #prefix>
                <user-outlined :style="{ color: 'rgba(0,0,0,.25)' }" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item name="password">
            <a-input-password
              size="large"
              placeholder="请输入密码"
              v-model:value="Logindata.password"
            >
              <template #prefix>
                <LockOutlined :style="{ color: 'rgba(0,0,0,.25)' }" />
              </template>
            </a-input-password>
          </a-form-item>
        </a-tab-pane>
      </a-tabs>

      <a-form-item>
        <a-checkbox v-model:checked="checked">自动登录</a-checkbox>
        <div style="float: right">
          <router-link class="register" style="margin-right: 24px" :to="{ name: 'register' }"
            >注册账户</router-link
          >
          <!-- 先用a标签代替 -->
          <a class="forge-password">忘记密码</a>
        </div>
      </a-form-item>

      <!-- 没屁用的一行 -->
      <a-form-item style="margin-top: 24px">
        <a-button
          size="large"
          type="primary"
          class="login-button"
          :loading="loginBtn"
          :disabled="loginBtn"
          htmlType="submit"
          >登录</a-button
        >
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup>
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
// import myaxios from '@/utils/myaxios'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
// 登录接口
import { UserLogin } from '@/api/user'
// pinia
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
// 路由
const router = useRouter()
// login的ref
const formLogin = ref()
// 登录按钮的loading状态和禁用状态
const loginBtn = ref(false)
// tab标签的key
const activeKey = ref('tab1')
// 账号密码错误的提示
const isLoginError = ref(false)
// 自动登录单选框
const checked = ref(false)
// 登录数据
const Logindata = ref({
  account: '',
  password: '',
})
// 登录规则
const LoginRules = {
  account: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
    { min: 5, max: 10, message: '用户名必须是5-10位的字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '密码必须是6-15位的非空字符', trigger: 'blur' },
  ],
}
const handleSubmit = async () => {
  // 要先判断是不是符合校验
  console.log(' Logindata.value', Logindata.value)
  loginBtn.value = true
  // 登录接口(这里之后接口改良，要给个error)
  const res = await UserLogin(Logindata.value)
  // .then((res) => {
  console.log('res', res)
  loginBtn.value = false
  if (res.code == 0) {
    userStore.SET_USERINFO(res.data)
    isLoginError.value = false
    console.log(userStore.userInfo)

    message.success('登录成功')
    // 路由跳转到首页
    router.push({ name: 'home' })
  } else {
    // console.log('err', err)
    message.error(res.description)
    // loginBtn.value = false
    isLoginError.value = true
  }

  // 错误的时候就传请求出现错误
  // requestFailed (err){
  //     this.isLoginError = true
  //     this.$notification['error']({
  //       message: '错误',
  //       description: ((err.response || {}).data || {}).message || '请求出现错误，请稍后再试',
  //       duration: 4
  //     })
  //   }
}
</script>

<style lang="less" scoped>
.user-layout-login {
  label {
    font-size: 14px;
  }

  .getCaptcha {
    display: block;
    width: 100%;
    height: 40px;
  }

  .forge-password {
    font-size: 14px;
  }

  button.login-button {
    padding: 0 15px;
    font-size: 16px;
    height: 40px;
    width: 100%;
  }

  .user-login-other {
    text-align: left;
    margin-top: 24px;
    line-height: 22px;

    .item-icon {
      font-size: 24px;
      color: rgba(0, 0, 0, 0.2);
      margin-left: 16px;
      vertical-align: middle;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: #1890ff;
      }
    }

    .register {
      float: right;
    }
  }
}
</style>
