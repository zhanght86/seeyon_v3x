package com.seeyon.v3x.organization.util;

import java.util.HashMap;
import java.util.Map;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;

interface EntityKeepable {
	V3xOrgMember getMemberById(long id) throws BusinessException;

	V3xOrgDepartment getDepartmentById(long id) throws BusinessException;

	V3xOrgPost getPostById(long id) throws BusinessException;

	V3xOrgLevel getLevelById(long id) throws BusinessException;

	V3xOrgAccount getAccountById(long id) throws BusinessException;
}

/**
 * 按Id取实体助手，缓存访问过的实体，再次访问时从缓存中读取。
 * 
 * @author wangwy
 * 
 */
public class EntityKeeper implements EntityKeepable {

	public EntityKeeper(OrgManager om) {
		initEntityMaps(om);
	}

	// 实体缓存
	interface V3xOrgEntityMap<T> {
		T get(long id) throws BusinessException;

		void remove(long id) throws BusinessException;
	}

	abstract class AbstractV3xOrgEntityMap<T> implements V3xOrgEntityMap {
		private Map<Long, T> map = new HashMap<Long, T>();
		protected final OrgManager om;

		public AbstractV3xOrgEntityMap(OrgManager om) {
			this.om = om;
		}

		public T get(long id) throws BusinessException {
			if (!map.containsKey(id)) {
				T e = getEntity(id);
				// if (e != null)
				// 允许空，空表示指定Id的实体不存在，下次访问时没有必要再去查询。
				map.put(id, e);
				return e;
			}
			return map.get(id);
		}

		public void remove(long id) throws BusinessException {
			map.remove(id);
		}

		protected abstract T getEntity(long id) throws BusinessException;

	}

	private V3xOrgEntityMap<V3xOrgAccount> accounts;
	private V3xOrgEntityMap<V3xOrgDepartment> departments;
	private V3xOrgEntityMap<V3xOrgPost> posts;
	private V3xOrgEntityMap<V3xOrgLevel> levels;
	private V3xOrgEntityMap<V3xOrgMember> members;

	@SuppressWarnings("unchecked")
	private void initEntityMaps(OrgManager om) {
		accounts = new AbstractV3xOrgEntityMap<V3xOrgAccount>(om) {
			protected V3xOrgAccount getEntity(long id) throws BusinessException {
				return om.getEntityById(V3xOrgAccount.class, id);
			}
		};

		departments = new AbstractV3xOrgEntityMap<V3xOrgDepartment>(om) {
			protected V3xOrgDepartment getEntity(long id)
					throws BusinessException {
				return om.getEntityById(V3xOrgDepartment.class, id);
			}
		};
		posts = new AbstractV3xOrgEntityMap<V3xOrgPost>(om) {
			protected V3xOrgPost getEntity(long id) throws BusinessException {
				return om.getEntityById(V3xOrgPost.class, id);
			}
		};
		levels = new AbstractV3xOrgEntityMap<V3xOrgLevel>(om) {
			protected V3xOrgLevel getEntity(long id) throws BusinessException {
				return om.getEntityById(V3xOrgLevel.class, id);
			}
		};
		members = new AbstractV3xOrgEntityMap<V3xOrgMember>(om) {
			protected V3xOrgMember getEntity(long id) throws BusinessException {
				return om.getEntityById(V3xOrgMember.class, id);
			}
		};
	}

	public V3xOrgAccount getAccountById(long id) throws BusinessException {
		return accounts.get(id);
	}

	public V3xOrgDepartment getDepartmentById(long id) throws BusinessException {

		return departments.get(id);
	}

	public V3xOrgLevel getLevelById(long id) throws BusinessException {

		return levels.get(id);
	}

	public V3xOrgMember getMemberById(long id) throws BusinessException {

		return members.get(id);
	}

	public V3xOrgPost getPostById(long id) throws BusinessException {

		return posts.get(id);
	}
}
