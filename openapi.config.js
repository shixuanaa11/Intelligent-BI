/**
 * openapi请求自动生成配置类
 */
import { generateService } from '@umijs/openapi'

generateService({
  requestLibPath: "import request from '@/utils/myaxios'",
  schemaPath: 'http://localhost:8081/api/v3/api-docs',
  serversPath: './src',
})
