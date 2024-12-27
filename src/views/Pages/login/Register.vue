<template>
  <div class="main user-layout-register">
    <h3>
      <span>注册</span>
    </h3>
    <a-form
      ref="formRegister"
      :form="form"
      id="formRegister"
      :rules="RegisterRules"
      :model="registerData"
      @finish="handleSubmit()"
    >
      <a-form-item name="account">
        <a-input
          size="large"
          type="text"
          placeholder="请输入账号"
          v-model:value="registerData.account"
        >
          <template #prefix>
            <user-outlined :style="{ color: 'rgba(0,0,0,.25)' }" />
          </template>
        </a-input>
      </a-form-item>

      <a-popover placement="rightTop" trigger="hover" v-model="popoverVisible">
        <template #content>
          <div :style="{ width: '240px' }">
            <div :class="['user-register', levelClass[passwordLevel]]">
              {{ levelNames[passwordLevel] }}
            </div>
            <a-progress
              :percent="passwoedPercent"
              :show-info="false"
              :stroke-color="levelColor[passwordLevel]"
            />
            <div style="margin-top: 10px">
              <span>请至少输入 8 个字符。请不要使用容易被猜到的密码 </span>
            </div>
          </div>
        </template>
        <a-form-item name="password">
          <a-input-password
            size="large"
            placeholder="请输入密码"
            v-model:value="registerData.password"
            @change="changeState(e)"
          >
            <template #prefix>
              <LockOutlined :style="{ color: 'rgba(0,0,0,.25)' }" />
            </template>
          </a-input-password>
        </a-form-item>
      </a-popover>

      <a-form-item name="checkPassword">
        <a-input-password
          size="large"
          placeholder="请再次输入密码"
          v-model:value="registerData.checkPassword"
        >
          <template #prefix>
            <LockOutlined :style="{ color: 'rgba(0,0,0,.25)' }" />
          </template>
        </a-input-password>
      </a-form-item>

      <!-- 手机号暂时不用加，可以后续加，我们先注释掉 -->
      <!-- <a-form-item>
          <a-input size="large" :placeholder="$t('user.login.mobile.placeholder')" v-decorator="['mobile', {rules: [{ required: true, message: $t('user.phone-number.required'), pattern: /^1[3456789]\d{9}$/ }, { validator: this.handlePhoneCheck } ], validateTrigger: ['change', 'blur'] }]">
            <a-select slot="addonBefore" size="large" defaultValue="+86">
              <a-select-option value="+86">+86</a-select-option>
              <a-select-option value="+87">+87</a-select-option>
            </a-select>
          </a-input>
        </a-form-item> -->

      <!--<a-input-group size="large" compact>
              <a-select style="width: 20%" size="large" defaultValue="+86">
                <a-select-option value="+86">+86</a-select-option>
                <a-select-option value="+87">+87</a-select-option>
              </a-select>
              <a-input style="width: 80%" size="large" placeholder="11 位手机号"></a-input>
            </a-input-group>-->

      <!-- 手机号都没有了，验证码也不要了 -->
      <!-- <a-row :gutter="16">
          <a-col class="gutter-row" :span="16">
            <a-form-item>
              <a-input size="large" type="text" :placeholder="$t('user.login.mobile.verification-code.placeholder')" v-decorator="['captcha', {rules: [{ required: true, message: '请输入验证码' }], validateTrigger: 'blur'}]">
                <a-icon slot="prefix" type="mail" :style="{ color: 'rgba(0,0,0,.25)' }"/>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col class="gutter-row" :span="8">
            <a-button
              class="getCaptcha"
              size="large"
              :disabled="state.smsSendBtn"
              @click.stop.prevent="getCaptcha"
              v-text="!state.smsSendBtn && $t('user.register.get-verification-code')||(state.time+' s')"></a-button>
          </a-col>
        </a-row> -->

      <a-form-item>
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="register-button"
          :loading="registerBtn"
          :disabled="registerBtn"
          >注册
        </a-button>
        <router-link class="login" :to="{ name: 'login' }">使用已有账号登录</router-link>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
// 接口
import { UserRegister } from '@/myapi/user'
import { message, notification } from 'ant-design-vue'
import { useRouter } from 'vue-router'
// 路由
const router = useRouter()
// 密码进度条的显示
const popoverVisible = ref(true)
const passwoedPercent = ref(0)
// 登录按钮的loading状态和禁用状态
const registerBtn = ref(false)
// 密码进度条的状态索引
const passwordLevel = ref(0)
// 注册的表单ref
const formRegister = ref()
// 注册的数据
const registerData = ref({
  account: '',
  password: '',
  checkPassword: '',
})
const RegisterRules = {
  account: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
    { min: 5, max: 10, message: '用户名必须是5-10位的字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { pattern: /^\S{8,}$/, message: '密码不能少于8位的非空字符', trigger: 'blur' },
  ],
  checkPassword: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { pattern: /^\S{8,}$/, message: '密码不能少于8位的非空字符', trigger: 'blur' },
    {
      required: true,
      validator: (rule, value, callback) => {
        if (value !== registerData.value.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}
// 注册按钮的事件
const handleSubmit = async () => {
  await formRegister.value.validate()
  registerBtn.value = true

  const res = await UserRegister(registerData.value)
  console.log(res)
  if (res.code == 0) {
    registerBtn.value = false
    message.success('注册成功')
    router.push({ name: 'login' })
  } else {
    registerBtn.value = false
  }
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
// }

// 密码进度条
const changeState = () => {
  console.log('scorePassword ; ', registerData.value.password)
  if (registerData.value.password.length >= 8) {
    if (scorePassword(registerData.value.password) >= 40) {
      passwordLevel.value = 1
    }
    if (scorePassword(registerData.value.password) >= 60) {
      passwordLevel.value = 2
    }
    if (scorePassword(registerData.value.password) >= 80) {
      passwordLevel.value = 3
    }
  } else {
    passwordLevel.value = 0
  }
  // this.state.passwordLevel = this.state.level
  passwoedPercent.value = passwordLevel.value * 33
}

// 这里定义几个参数来控制密码强度进度条的演示
const levelColor = ref({
  0: '#ff0000',
  1: '#ff0000',
  2: '#ff7e05',
  3: '#52c41a',
})
const levelNames = ref({
  0: '强度：太短',
  1: '强度：低',
  2: '强度：中',
  3: '强度：强',
})
const levelClass = ref({
  0: 'error',
  1: 'error',
  2: 'warning',
  3: 'success',
})
// 密码进度条的状态

// 登录密码进度条的校验规则
const scorePassword = (pass) => {
  let score = 0
  if (!pass) {
    return score
  }
  // award every unique letter until 5 repetitions
  const letters = {}
  for (let i = 0; i < pass.length; i++) {
    letters[pass[i]] = (letters[pass[i]] || 0) + 1
    score += 5.0 / letters[pass[i]]
  }

  // bonus points for mixing it up
  const variations = {
    digits: /\d/.test(pass),
    lower: /[a-z]/.test(pass),
    upper: /[A-Z]/.test(pass),
    nonWords: /\W/.test(pass),
  }

  let variationCount = 0
  for (var check in variations) {
    variationCount += variations[check] === true ? 1 : 0
  }
  score += (variationCount - 1) * 10
  console.log(score)

  return parseInt(score)
}
</script>
<style lang="less" scoped>
.user-register {
  &.error {
    color: #ff0000;
  }

  &.warning {
    color: #ff7e05;
  }

  &.success {
    color: #52c41a;
  }
}

.user-layout-register {
  .ant-input-group-addon:first-child {
    background-color: #fff;
  }
}
</style>
<style lang="less" scoped>
.user-layout-register {
  & > h3 {
    font-size: 16px;
    margin-bottom: 20px;
  }

  .getCaptcha {
    display: block;
    width: 100%;
    height: 40px;
  }

  .register-button {
    width: 50%;
  }

  .login {
    float: right;
    line-height: 40px;
  }
}
</style>
