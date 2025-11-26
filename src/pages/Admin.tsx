import { PageContainer } from '@ant-design/pro-components';
import { Outlet } from '@umijs/max';
import React from 'react';
const Admin: React.FC = () => {
  return (
    <PageContainer content={' 这个页面只有 admin 权限才能查看'}>
      <Outlet />
    </PageContainer>
  );
};
export default Admin;
