前后端交互：
    前端通过以下技术与后端交互：
    1.AJAX
    2.AXIOS
    3.request:ant design pro中使用的请求库，封装了AJAX，提供了更方便的请求方式(更优于AXIOS)。

ant design pro项目中是通过app.ts文件来配置请求库的，如下所示：
export const request: RequestConfig = {
  prefix: 'localhost:8080',  # 后端服务地址
};

前端使用的端口是8000，后端使用的端口是8080。-----存在跨域问题
解决办法：
    WAY-1.在后端服务中添加跨域配置，允许前端服务的跨域请求。
    WAY-2.在前端服务中添加代理配置，将跨域请求代理到后端服务。---当前项目中使用
   
代理：
  正向代理：客户端请求代理服务器，由正向代理服务器转发到目标服务器。(替客户端发送请求)
  反向代理：客户端请求代理服务器，由代理服务器转发到目标服务器。(替服务器接收请求) ----当前使用的代理方式


  开启ant design pro的代理配置：
  '/api/': {
      // 要代理的地址
      target: 'http://localhost:8080', // 一定是http而不是https，因为代理服务器要和前端服务在同一协议下
      // 配置了这个可以从 http 代理到 https
      // 依赖 origin 的功能可能需要这个，比如 cookie
      changeOrigin: true,
    },


一、前端技术注意项：
  1. 当在pages目录下创建了register目录，并在router.ts文件中配置了register目录的路由.
     但是要注意外部的app.tsx文件(当前ant-design-pro项目中的一个全局入口文件)中的onPageChange()方法中
      const { location } = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
   如果用户没有登录，则会被重定向到login页面。
   可以在该部分代码中新建一个白名单
   whiteList = ['user/register',LoginpPath];
   // 如果不在白名单中，且没有登录，重定向到 login
    if (!initialState?.currentUser && !whiteList.includes(location.pathname)) {
       history.push(loginPath);
    }
  2. ant design组件库封装了React
     ant design procomponents封装了ant design组件库
     ant design pro后台管理系统 由 ant design、ant design procomponents以及其他的库组成。
  3. ===与！===
     === 严格等于，比较两个变量的值和类型是否相等。
     !== 严格不等于，比较两个变量的值和类型是否不相等。
  4. src/services/api.ts
     在其中定义了所有向后端发送请求的函数。
     其中的return request()方法中的data表示的是返回的数据类型，可以点击进去根据后台的数据类型修改。
   5. src/access.ts:用于控制用户的访问权限,canAdmin: currentUser && currentUser.userRole === 1,
      在src/pages/Admin目录下的页面中，只有当用户是管理员时才能访问。
   6. 每当前端页面刷新时，都会执行app.tsx文件中的getInitialState()方法。该方法中有一个queryCurrentUser()方法，该方法向后端发送请求获取当前用户信息。------后端注意事项 1
   7. 在Admin.tsx中引入子路由，，需要使用 Outlet。
      children 在 React Router v6 的路由组件中不再自动传入，嵌套路由应通过 Outlet 渲染。
   8. ProTable中Columns的属性介绍：
      title: 列标题
      dataIndex: 列数据在数据中的字段
      valueType: 列数据的类型，例如 'date', 'money','avatar' 等
      copyable: 是否可复制
      ellipsis: 是否显示省略号
      tooltip: 鼠标悬停时的提示信息
      formItemProps: 表单项属性，用于配置表单控件的额外属性---例如是否必填项等等
   







二、后端技术注意项：
  1. 除了基础的CRUD接口外，还要开发一个接收用户登录态的接口。用来记录用户登陆状态，当登陆一次之后，就不必再次登录。


三、代码优化
   前端
   全局请求响应拦截器封装(对接后端返回值，接收data)
   作用：
      * 对所有请求的响应进行统一处理，例如：
        * 对响应数据进行解析，提取出业务数据
        * 对响应状态码进行判断，根据状态码进行不同的处理
        * 对响应数据进行格式化，例如：将日期字符串转换为日期对象
   实现：
      * 使用axios的interceptors.response.use()方法，在响应拦截器中对响应数据进行处理。

   后端
   1. 通用返回对象，给对象补充信息，告诉前端该请求在业务上是否成功，例如：
      {
        code: 200, // 业务上的成功状态码
        msg: 'success', // 业务上的成功信息
        data: {
         name:'张三',
         age:18,
         ...
        }, // 业务上返回的数据
      }
      common下的ResultUtil中定义方法。返回成功的方法success()，用于返回成功的结果；
      并在common下的ResultUtil中定义返回失败的方法error()，用于返回失败的结果。
   2. 全局异常处理
      1.自定义错误码：在后端common下定义一个ErrorCode枚举类，用于表示业务上的异常状态码和异常信息。
      2.封装全局异常处理：在后端common/exception下定义一个GlobalExceptionHandler类，用于处理全局异常。
        2.1 定义业务异常类
           * 相较于Java异常类支持更多字段，更灵活(common/BaseResponse)
           * 自定义构造方法，支持传入自定义的错误码和错误信息(common/ErrorCode)

        2.2 编写全局异常处理器(common/exception/GlobalExceptionHandler.java)
        作用：
           * 捕获所有异常，并返回统一的响应结果,让前端获取到更详细的业务报错信息
           * 屏蔽掉项目框架中本身的异常信息(不暴露服务器内部状态)，只暴露给前端自定义的业务报错信息
           * 集中记录日志信息，便于后续排查问题

         实现：
            * SpringAOP:在方法前后分别进行额外处理

   3. 全局请求日志与登录校验


四、项目部署上线
   1.原生部署
   2.docker部署
   3.绑定域名