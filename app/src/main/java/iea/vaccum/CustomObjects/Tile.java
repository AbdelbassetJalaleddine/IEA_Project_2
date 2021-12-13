package iea.vaccum.CustomObjects;

public class Tile {

    private boolean mRightWall,mDownWall,mVaccumCleaner,mDirt,mSelected,dirtProducers;


    public Tile(boolean mRightWall, boolean mDownWall, boolean mVaccumCleaner, boolean mDirt, boolean mSelected) {
        this.mRightWall = mRightWall;
        this.mDownWall = mDownWall;
        this.mVaccumCleaner = mVaccumCleaner;
        this.mDirt = mDirt;
        this.mSelected = mSelected;
    }
    public Tile(boolean mRightWall, boolean mDownWall, boolean mVaccumCleaner,boolean dirtProducers, boolean mDirt, boolean mSelected) {
        this.mRightWall = mRightWall;
        this.mDownWall = mDownWall;
        this.mVaccumCleaner = mVaccumCleaner;
        this.mDirt = mDirt;
        this.mSelected = mSelected;
        this.dirtProducers = dirtProducers;
    }

    public boolean isDirtProducers() {
        return dirtProducers;
    }

    public void setDirtProducers(boolean dirtProducers) {
        this.dirtProducers = dirtProducers;
    }

    public boolean ismRightWall() {
        return mRightWall;
    }

    public void setmRightWall(boolean mRightWall) {
        this.mRightWall = mRightWall;
    }

    public boolean ismDownWall() {
        return mDownWall;
    }

    public void setmDownWall(boolean mDownWall) {
        this.mDownWall = mDownWall;
    }

    public boolean ismVaccumCleaner() {
        return mVaccumCleaner;
    }

    public void setmVaccumCleaner(boolean mVaccumCleaner) {
        this.mVaccumCleaner = mVaccumCleaner;
    }

    public boolean ismDirt() {
        return mDirt;
    }

    public void setmDirt(boolean mDirt) {
        this.mDirt = mDirt;
    }

    public boolean ismSelected() {
        return mSelected;
    }

    public void setmSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
