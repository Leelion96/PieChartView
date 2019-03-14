package github.leelion96.piechartview.Entity;

/**
 * @author LiCheng
 * @date 2019/2/22
 */
public class PieEntry {

    //颜色
    private int color;
    //比分比
    private float percentage;
    //条目名
    private String label;
    //扇区起始角度
    private float currentStartAngle;
    //扇区总角度
    private float sweepAngle;

    public PieEntry(float percentage, String label) {
        this.percentage = percentage;
        this.label = label;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public float getCurrentStartAngle() {
        return currentStartAngle;
    }

    public void setCurrentStartAngle(float currentStartAngle) {
        this.currentStartAngle = currentStartAngle;
    }
}
