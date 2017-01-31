package com.seeyon.v3x.common.comboTree;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 下拉框树型结构工具类
 */
public class ComboTreeUtils {

	/**
	 * 获取所有节点关系
	 * 
	 * @param map 节点集合
	 * @param node 当前节点
	 */
	public static void findParentNode(Map<String, ComboTreeNode> map, ComboTreeNode node, List<String> roots) {
		if (StringUtils.isNotBlank(node.getParentId())) {
			ComboTreeNode parentNode = map.get(node.getParentId());

			if (parentNode != null && parentNode.getChildren() != null) {
				if (!parentNode.getChildren().contains(node)) {
					parentNode.getChildren().add(node);
				}
				ComboTreeUtils.findParentNode(map, parentNode, roots);
			} else if (!"-1".equals(node.getParentId())) {// 单位访问权限控制（上级、平级、下级访问）
				if (!roots.contains(node.getId())) {
					roots.add(node.getId());
				}
			}
		}
	}

}
