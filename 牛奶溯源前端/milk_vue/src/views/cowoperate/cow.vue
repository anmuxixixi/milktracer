<template>
  <div class="app-container">

    <h3>请选择你的操作</h3>

    <div>
      <el-radio v-model="operate" label="0" border>喂养</el-radio>
      <el-radio v-model="operate" label="1" border>挤奶</el-radio>
      <el-radio v-model="operate" label="2" border>检疫</el-radio>
    </div>

    <div style="border:1px solid ; margin-top: 10px; width: 450px;">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="100px" style="margin: 20px">
        <el-form-item label="奶牛ID">
          <el-input v-model="temp.cowId"></el-input>
        </el-form-item>
        <el-form-item label="操作">
          <el-input :value="operationName[operate]" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="消耗/产出">
          <el-input v-model="temp.consumptionOrOutput"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="operateCow">确定</el-button>
          <el-button @click="resetTemp">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { operateData } from '@/api/operate'
import waves from '@/directive/waves' // waves directive
import { parseTime } from '@/utils'

const calendarTypeOptions = [
  { key: 'CN', display_name: 'China' },
  { key: 'US', display_name: 'USA' },
  { key: 'JP', display_name: 'Japan' },
  { key: 'EU', display_name: 'Eurozone' }
]

// arr to obj, such as { CN : "China", US : "USA" }
const calendarTypeKeyValue = calendarTypeOptions.reduce((acc, cur) => {
  acc[cur.key] = cur.display_name
  return acc
}, {})

export default {
  name: 'ComplexTable',
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    },
    typeFilter(type) {
      return calendarTypeKeyValue[type]
    }
  },
  data() {
    return {
      operationName: ['喂养', '挤奶', '检疫'],
      operate: '0',
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        importance: undefined,
        title: undefined,
        type: undefined
      },
      importanceOptions: [1, 2, 3],
      calendarTypeOptions,
      statusOptions: ['published', 'draft', 'deleted'],
      showReviewer: false,
      temp: {
        cowId: '',
        operation: '',
        consumptionOrOutput: ''
      },
      pvData: [],
      downloadLoading: false
    }
  },
  created() {
  },
  methods: {
    operateCow() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.temp.operation = this.operate
          operateData(this.temp).then(() => {
            this.list.unshift(this.temp)
            this.$notify({
              title: 'Success',
              message: '奶牛操作成功',
              type: 'success',
              duration: 2000
            })
            this.resetTemp()
          })
        }
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleModifyStatus(row, status) {
      this.$message({
        message: '操作Success',
        type: 'success'
      })
      row.status = status
    },
    resetTemp() {
      this.temp = {
        cowId: '',
        operation: '',
        consumptionOrOutput: ''
      }
    },
    formatJson(filterVal) {
      return this.list.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    },
    getSortClass: function(key) {
      const sort = this.listQuery.sort
      return sort === `+${key}` ? 'ascending' : 'descending'
    },
    headClass() { // 表头居中显示
      return 'text-align:center'
    }
  }
}
</script>
