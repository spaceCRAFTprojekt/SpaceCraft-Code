package menu;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
public abstract class Slider extends JSlider{
    //Es wäre nett, wenn den jemand hernehmen würde, um im workspace dtime und manoeuvre.t0/manoeuvre.t1 einzustellen. -LG
    public Slider(String value, int min, int max, int startValue){
        super(JSlider.HORIZONTAL,min,max,startValue);
        this.setMajorTickSpacing(10);
        this.setMinorTickSpacing(1);
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                stateChanged(e);
            }
        });
    }
    public abstract void stateChanged(ChangeEvent e);
} 