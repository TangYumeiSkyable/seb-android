package com.rihuisoft.loginmode.crop;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.View;

/**
 * @author JianTao.Young
 * @time: 2015-1-28 下午5:56:28
 */
public class CircleHighlightView extends HighlightView {

    public CircleHighlightView(View context) {
        super( context);
    }

    @Override
    protected void draw(Canvas canvas) {
    	try {
    		canvas.save();
            Path path = new Path();
            outlinePaint.setStrokeWidth( outlineWidth);
            if(!hasFocus()) {//Didn't focus, draw a black rectangle directly
                outlinePaint.setColor( Color.BLACK);
                canvas.drawRect( drawRect, outlinePaint);
            }
            else {
                Rect viewDrawingRect = new Rect();
                viewContext.getDrawingRect( viewDrawingRect);

                //Has cut casing drawRect, calculate the radius of the circle
                float radius = (drawRect.right - drawRect.left) / 2;
                //Add a circle
                path.addCircle( drawRect.left + radius, drawRect.top + radius, radius, Direction.CW);
                outlinePaint.setColor( highlightColor);

                //Cut out the canvas, the path outside area, in order to outsidePaint filled in
                canvas.clipPath( path, Region.Op.DIFFERENCE);
                canvas.drawRect( viewDrawingRect, outsidePaint);

                canvas.restore();
                //Draw the circle line, defined here outlinePaint Paint. Style. STROKE: said only draw the outline of the geometry.
                canvas.drawPath( path, outlinePaint);

                //When modifyMode turns up, draw handles, also is the four small circle
                if(handleMode == HandleMode.Always || (handleMode == HandleMode.Changing && modifyMode == ModifyMode.Grow)) {
                    drawHandles( canvas);
                }
            }
    	} catch(Exception e) {
			e.printStackTrace();
		} 
        
    }

    // Determines which edges are hit by touching at (x, y)
    @Override
    public int getHit(float x, float y) {
        return getHitOnCircle( x, y);
    }

    @Override
    void handleMotion(int edge, float dx, float dy) {
    	try {
    		Rect r = computeLayout();
            if(edge == MOVE) {
                // Convert to image space before sending to moveBy()
                moveBy( dx * (cropRect.width() / r.width()), dy * (cropRect.height() / r.height()));
            }
            else {
                if(((GROW_LEFT_EDGE | GROW_RIGHT_EDGE) & edge) == 0) {
                    dx = 0;
                }

                if(((GROW_TOP_EDGE | GROW_BOTTOM_EDGE) & edge) == 0) {
                    dy = 0;
                }
                //The change of direction will take its for your reference;The default reference dx
                if(Math.abs( dx) < Math.abs( dy)) {
                    dx = 0.0f;// Dx is set to 0, growBy dy with reference, calculated in accordance with the 1:1 dx
                }
                float xDelta = dx * (cropRect.width() / r.width());
                float yDelta = dy * (cropRect.height() / r.height());
                growBy( (((edge & GROW_LEFT_EDGE) != 0) ? -1 : 1) * xDelta, (((edge & GROW_TOP_EDGE) != 0) ? -1 : 1) * yDelta);
            }
    	} catch(Exception e) {
			e.printStackTrace();
		} 
        
    }

    /**
     * According to the x, y coordinates, calculate its relationship with the circle (circle, circle, circle)
     * @param x
     * @param y
     * @return
     */
    private int getHitOnCircle(float x, float y) {
    	int retval = GROW_NONE;
    	try {
    		Rect r = computeLayout();
            
            final float hysteresis = 20F;
            int radius = (r.right - r.left) / 2;

            int centerX = r.left + radius;
            int centerY = r.top + radius;

            //To determine whether a touch position on the circle
            float ret = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);
            double rRadius = Math.sqrt( ret);
            double gap = Math.abs( rRadius - radius);

            if(gap <= hysteresis) {// On the round.Because here is inheritance to HighlightView (draw a rectangle box) to deal with, so the simulation returned to the up and down or so, rather than on pure circle, close test available.You can also custom.
                if(x < centerX) {// left
                    retval |= GROW_LEFT_EDGE;
                }
                else {
                    retval |= GROW_RIGHT_EDGE;
                }

                if(y < centerY) {// up
                    retval |= GROW_TOP_EDGE;
                }
                else {
                    retval |= GROW_BOTTOM_EDGE;
                }
            }
            else if(rRadius > radius) {// outside
                retval = GROW_NONE;
            }
            else if(rRadius < radius) {// inside，The circle is executed move
                retval = MOVE;
            }

    	} catch(Exception e) {
			e.printStackTrace();
		} 
        
        return retval;
    }

}
