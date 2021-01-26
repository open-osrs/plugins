/*
 * Copyright (c) 2020, BegOsrs <https://github.com/begosrs>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package begosrs.barbarianassault.attackstyle;

import begosrs.barbarianassault.api.widgets.BaWidgetInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttackStyleWidget
{
	ONE(BaWidgetInfo.COMBAT_STYLE_ONE, BaWidgetInfo.COMBAT_STYLE_ONE_ICON, BaWidgetInfo.COMBAT_STYLE_ONE_TEXT),
	TWO(BaWidgetInfo.COMBAT_STYLE_TWO, BaWidgetInfo.COMBAT_STYLE_TWO_ICON, BaWidgetInfo.COMBAT_STYLE_TWO_TEXT),
	THREE(BaWidgetInfo.COMBAT_STYLE_THREE, BaWidgetInfo.COMBAT_STYLE_THREE_ICON, BaWidgetInfo.COMBAT_STYLE_THREE_TEXT),
	FOUR(BaWidgetInfo.COMBAT_STYLE_FOUR, BaWidgetInfo.COMBAT_STYLE_FOUR_ICON, BaWidgetInfo.COMBAT_STYLE_FOUR_TEXT);

	private final BaWidgetInfo containerWidget;
	private final BaWidgetInfo iconWidget;
	private final BaWidgetInfo textWidget;

	@Getter
	private static final AttackStyleWidget[] attackStyles;

	static
	{
		attackStyles = new AttackStyleWidget[values().length];
		int i = 0;
		for (AttackStyleWidget attackStyleWidget : values())
		{
			attackStyles[i] = attackStyleWidget;
			i++;
		}
	}
}
