export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './user/login' },
      { name: '注册', path: '/user/register', component: './user/register' }],
  },
  { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    component: "./Admin",
    routes: [
      { path: '/admin/user-manage', name: '用户管理', component: './Admin/UserManage' },
      {component: './404'}
    ],
  },
  { name: '查询表格', icon: 'table', path: '/list', component: './table-list' },
  { path: '/', redirect: '/user/login' },
  { path: '*', layout: false, component: './404' },
];
