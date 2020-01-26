package com.tcd.yaatra.repository.models;

public class AsyncData<DataClass> {

    public enum State {
        LOADING,
        SUCCESS,
        FAILURE
    }

    private DataClass data;
    private State state;


    private AsyncData(DataClass data, State state){
        this.data = data;
        this.state = state;
    }

    public static <T> AsyncData<T> getLoadingState(){
        return new AsyncData<>(null, State.LOADING);
    }


    public static <T> AsyncData<T> getSuccessState(T data){
        return new AsyncData<>(data, State.SUCCESS);
    }

    public static <T> AsyncData<T> getFailureState(T data){
        return new AsyncData<>(data, State.FAILURE);
    }


    public DataClass getData() {
        return data;
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return "AsyncData{" +
                "data=" + data +
                ", state=" + state +
                '}';
    }
}
