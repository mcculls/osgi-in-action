<html>
<body bgcolor="white">
<%@ taglib uri="/mut" prefix="mut" %>
<jsp:useBean id="cart" class="com.taglib.wdjsp.commontasks.ShoppingCart"
             scope="session"/>

<jsp:setProperty name="cart" property="addItem" param="addItem"/>
<jsp:setProperty name="cart" property="removeItem" param="removeItem"/>

<h2>Your Shopping Cart</h2>
<table border="1">
<tr><th>Item #</th><th>Description</th><th>Qty.</th>
<th>Unit Price</th><th>Extended Price</th><th>&nbsp;</th></tr>
<mut:forProperty name="cart" property="item" id="item"
class="com.taglib.wdjsp.commontasks.ShoppingCartItem">
<tr>
<td><jsp:getProperty name="item" property="itemNumber"/></td>
<td><jsp:getProperty name="item" property="description"/></td>
<td><jsp:getProperty name="item" property="count"/></td>
<td><jsp:getProperty name="item" property="unitPriceString"/></td>
<td><jsp:getProperty name="item" property="extendedPriceString"/></td>
<td><a href="shoppingcart.jsp?removeItem=<jsp:getProperty name="item"
property="itemNumber"/>">remove</a></td>
</tr>
</mut:forProperty>
<tr>
<td align="right" colspan="4"><b>Total:</b></td>
<td><jsp:getProperty name="cart" property="totalPrice"/></td>
<td>&nbsp;</td>
</tr>
</table>

<p>
<a href="catalog.jsp">Return To Catalog</a>
</body>
</html>

