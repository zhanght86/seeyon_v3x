/**
 * 去除Office转换的修订样式，追加到Word转换后页面的末尾
 */
<!--
var sss = document.styleSheets;
var lastestStyleSheet = sss.length > 0 ? sss[sss.length-1] : document.createStyleSheet();
function addRule(sSelector, sStyle) {
	if (lastestStyleSheet.insertRule) { // 标准，IE9
		lastestStyleSheet.insertRule(sSelector + " {" + sStyle + "}", lastestStyleSheet.cssRules.length);
	} else { // IE8及之前
		lastestStyleSheet.addRule(sSelector, sStyle);
	}
}
function removeRule(selectorText) {
	var st = selectorText.toLowerCase();
	for (var i=0; i<document.styleSheets.length; i++) {
		var styleSheet = document.styleSheets[i];
		if (styleSheet.cssRules) { // 标准，IE9
			var crs = styleSheet.cssRules;
			for (var j=0; j<crs.length; j++) {
				var selectorText = crs[j].cssText.substr(0, crs[j].cssText.indexOf("{"));
				selectorText = selectorText.replace(/(^\s*)|(\s*$)/g, ""); // trim
				if (selectorText.toLowerCase() == st) {
					styleSheet.deleteRule(j);
				}
			}
		} else { // IE8及之前
			var crs = styleSheet.rules;
			for (var j=0; j<crs.length; j++) {
				var selectorText = crs[j].selectorText;
				selectorText = selectorText.replace(/(^\s*)|(\s*$)/g, ""); // trim
				if (selectorText.toLowerCase() == st) {
					styleSheet.removeRule(j);
				}
			}
		}
	}
}
function setInheritedStyleProperty(node, property, defaultValue) {
	try {
		node.style[property] = "inherit";
	} catch (e) { // IE
		var inheritedValue;
		for (var p=node;p!=document.body;) {
			p = p.parentNode;
			if (p.style[property]) {
				inheritedValue = p.style[property];
				break;
			}
		}
		node.style[property] = inheritedValue ? inheritedValue : defaultValue;
	}
}
function removeStyleOfWordReviewTag() {
	// 批注
	addRule(".msocomanchor", "display: none");
	addRule(".msocomtxt", "display: none");
	// 删除
	addRule("del", "display: none");
	// 添加
	removeRule("span.msoins");
	var insTags = document.getElementsByTagName("ins");
	for (var i=0; i<insTags.length; i++) {
		setInheritedStyleProperty(insTags[i], "textDecoration", "none");
	}
	// 修改属性（字体等）
	removeRule("span.msochangeprop");
}
function isWordDocument() {
	var metas = document.getElementsByTagName("meta");
	for (var i=0; i<metas.length; i++) {
		if (metas[i].name === "ProgId" && metas[i].content === "Word.Document") {
			return true;
		}
	}
	return false;
}
if (isWordDocument()) {
	removeStyleOfWordReviewTag();
}
//-->
