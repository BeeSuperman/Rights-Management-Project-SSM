package com.taobao.service.imp;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.taobao.domain.Employee;
import com.taobao.domain.PageListRes;
import com.taobao.domain.QueryVo;
import com.taobao.domain.Role;
import com.taobao.mapper.EmployeeMapper;
import com.taobao.service.EmployeeService;
import org.apache.poi.util.SystemOutLogger;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public PageListRes selectAll(QueryVo vo) {
        /*调用mapper 查询员工 */
        Page<Object> page = PageHelper.startPage(vo.getPage(),vo.getRows());
        List<Employee> employees = employeeMapper.selectAll(vo);
        /*封装成pageList*/
        PageListRes pageListRes = new PageListRes();
        pageListRes.setTotal(page.getTotal());
        pageListRes.setRows(employees);
        return pageListRes;
    }
    /*保存员工*/
    @Override
    public void saveEmployee(Employee employee) {
        /*把密码进行加密*/
        Md5Hash md5Hash = new Md5Hash(employee.getPassword(), employee.getUsername(), 2);
        employee.setPassword(md5Hash.toString());
  /*      1.插入新员工*/
        employeeMapper.insert(employee);

       /* 2.建立员工与角色的关系*/
        for(Role roles : employee.getRoles()){
            employeeMapper.insertEmployeeAndRoleRel(employee.getId(),roles.getRid());
        }
    }
    /*更新员工*/
    @Override
    public void updateEmployee(Employee employee) {
        /*打破与角色之间关系*/
        employeeMapper.deleteRoleRel(employee.getId());
        /*重新建立角色的关系*/
        for (Role role : employee.getRoles()) {
            employeeMapper.insertEmployeeAndRoleRel(employee.getId(),role.getRid());
        }
        employeeMapper.updateByPrimaryKey(employee);
    }

    /*设置员工离职状态*/
    @Override
    public void updateState(Long id) {
        employeeMapper.updateState(id);
    }
    /*根据用户名当中查询有没有当前用户*/
    @Override
    public Employee getEmployeeWithUserName(String username) {
        return  employeeMapper.getEmployeeWithUserName(username);
    }
    /*根据用户的id查询角色编号名称*/
    @Override
    public List<String> getRolesById(Long id) {
        return employeeMapper.getRolesById(id);
    }
    /*根据用户的id查询权限 资源名称*/
    @Override
    public List<String> getPermissionById(Long id) {
        return employeeMapper.getPermissionById(id);
    }
}
