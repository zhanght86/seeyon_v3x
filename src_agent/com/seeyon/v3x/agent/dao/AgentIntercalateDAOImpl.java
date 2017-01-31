/**
 * 
 */
package com.seeyon.v3x.agent.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * @author jincm
 *
 */
public class AgentIntercalateDAOImpl extends BaseHibernateDao<V3xAgent> 
	implements AgentIntercalateDAO {

	@SuppressWarnings("unchecked")
	public List<V3xAgent> findAllAvailability() throws Exception {
		Date now = new Date();
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgent.class)
		.add(Expression.ge("endDate", now))
		.add(Expression.eq("cancelFlag", false))
		.addOrder(Order.desc("createDate"))
		;
		return super.executeCriteria(criteria, -1, -1);
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xAgent> findAvailabilityByAgentToId(Long agentToId,String... type) throws Exception{
		Date now = new Date();
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgent.class)
		.add(Expression.eq("agentToId", agentToId))
		.add(Expression.ge("endDate", now))
		.add(Expression.eq("cancelFlag", false))
		.addOrder(Order.desc("createDate"));
		if(type!=null&& type.length>0 && "agent".equals(type[0])){
			criteria.add(Expression.eq("agentToRemind", true));
		}
		List<V3xAgent> agents = super.executeCriteria(criteria, -1, -1);
		this.fillAgentDetail(agents);
		return agents;
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xAgent> findAvailabilityByAgentId(Long agentId,String... type) throws Exception{
		Date now = new Date();
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgent.class)
		.add(Expression.eq("agentId", agentId))
		.add(Expression.ge("endDate", now))
		.add(Expression.eq("cancelFlag", false))
		.addOrder(Order.desc("createDate"));
		if(type!=null && type.length>0 && "agent".equals(type[0])){
			criteria.add(Expression.eq("agentRemind", true));
		}
		List<V3xAgent> agents = super.executeCriteria(criteria, -1, -1);
		this.fillAgentDetail(agents);
		return agents;
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xAgent> findHistoryByAgentToId(Long agentToId) throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgent.class)
		.add(Expression.eq("agentToId", agentToId))
		.add(Expression.eq("cancelFlag", true))
		.addOrder(Order.desc("createDate"))
		;
		return super.executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xAgent> findHistoryByAgentId(Long agentId) throws Exception{
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgent.class)
		.add(Expression.eq("agentId", agentId))
		.add(Expression.eq("cancelFlag", true))
		.addOrder(Order.desc("createDate"))
		;
		return super.executeCriteria(criteria);
	}
	
	public V3xAgent getById(Long id) throws Exception{
		return get(id);
	}
	//yangzd
	public List<V3xAgent> getIsagentByToId(Long agentToId,Date fromDate,Date endDate,String agentOption) throws Exception
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgent.class)
		.add(Expression.eq("agentToId", agentToId))
		.add(Expression.eq("cancelFlag", false))
		.add(Expression.ge("startDate", fromDate))
		.add(Expression.le("endDate", endDate))
		.add(Expression.like("agentOption", agentOption,MatchMode.ANYWHERE))
		.addOrder(Order.desc("createDate"))
		;
		return super.executeCriteria(criteria);
	}

	public List<V3xAgent> listAgents(List<Long> agentIds){
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgent.class);
		criteria.add(Expression.in("id", agentIds));
		return super.executeCriteria(criteria,-1,-1);
	}
	
	/**
	 * 手动填充代理明细
	 * @param agents
	 */
	public void fillAgentDetail(List<V3xAgent> agents){
		if(agents == null  || agents.isEmpty())
			return;
		List<Long> ids = new ArrayList<Long>(agents.size());
		for(V3xAgent agent:agents){
			ids.add(agent.getId());
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgentDetail.class);
		criteria.add(Expression.in("agentId", ids));
		List<V3xAgentDetail> details = super.executeCriteria(criteria,-1,-1);
		if(details != null){
			List<V3xAgentDetail> agentDetails = null;
			for(V3xAgentDetail detail:details){
				for(V3xAgent agent:agents){
					if(detail.getAgentId().equals(agent.getId())){
						agentDetails = agent.getAgentDetails();
						if(agentDetails == null){
							agentDetails = new ArrayList<V3xAgentDetail>();
							agent.setAgentDetails(agentDetails);
						}
						agentDetails.add(detail);
						break;
					}
				}
			}
		}
	}
	
	
	public V3xAgent findAgentWithDetailById(V3xAgent agent){
		DetachedCriteria criteria = DetachedCriteria.forClass(V3xAgentDetail.class);
		criteria.add(Expression.eq("agentId", agent.getId()));
		List<V3xAgentDetail> details = super.executeCriteria(criteria,-1,-1);
		if(details != null){
			agent.setAgentDetails(details);
		}
		return agent;
	}
}
